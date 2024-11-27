package com.web.stard.global.config.aws;

import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Manager {

    private final S3Client s3Client;

    private final S3Config amazonConfig;

    // 단일 파일 업로드
    public String uploadFile(String keyName, MultipartFile file) {
        // 원본 파일 이름 가져오기
        String originalFilename = file.getOriginalFilename();
        // 확장자 가져오기
        String extension;
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            extension = "";
        }

        // PutObjectRequest를 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(amazonConfig.getBucket())
                .key(keyName + extension)
                .contentType(file.getContentType())
                .build();

        try {
            // S3 API 메소드(putObject)로 파일 Stream을 열어서 S3에 파일 업로드
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            // S3에 업로드된 파일의 URL을 가져오기
            URL url = s3Client.utilities().getUrl(b -> b.bucket(amazonConfig.getBucket()).key(keyName + extension));
            return url.toString();
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.UPLOAD_FAILED);
        }
    }

    // 다중 파일 업로드
    public List<String> uploadFiles(List<String> keyNames, List<MultipartFile> files) {
        if (files.size() != keyNames.size()) {
            throw new CustomException(ErrorCode.SIZE_MISMATCH);
        }

        List<String> uploadedFileUrls = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String fileUrl = uploadFile(keyNames.get(i), files.get(i));
            uploadedFileUrls.add(fileUrl);
        }

        return uploadedFileUrls;
    }

    // 단일 파일 삭제
    public void deleteFile(String fileUrl) {
        try {
            // 파일 URL에서 버킷 이름과 키를 추출
            URL url = new URL(fileUrl);
            String bucket = url.getHost().split("\\.")[0];
            String key = url.getPath().substring(1);

            // DeleteObjectRequest를 생성하여 파일 삭제
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (IOException e) {
            log.error("error at S3Manager deleteFile: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.DELETE_FAILED);
        }
    }

    // 다중 파일 삭제
    public void deleteFiles(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    // profile 디렉토리
    public String generateProfileKeyName(UUID uuid) {
        return amazonConfig.getProfilePath() + '/' + uuid.toString();
    }

    // chat 디렉토리
    public String generateChatKeyName(UUID uuid) {
        return amazonConfig.getChatPath() + '/' + uuid.toString();
    }

    // studyPost 디렉토리
    public String generateStudyPostKeyName(UUID uuid) {
        return amazonConfig.getStudyPostPath() + '/' + uuid.toString();
    }

    // 단일 파일 다운로드
    public InputStream downloadFile(String fileUrl) {
        try {
            // 파일 URL에서 버킷 이름과 키를 추출
            URL url = new URL(fileUrl);
            String bucket = url.getHost().split("\\.")[0];
            String key = url.getPath().substring(1);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return s3Client.getObject(getObjectRequest);
        } catch (IOException e) {
            log.error("error at S3Manager downloadFile: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.DOWNLOAD_FAILED);
        }
    }
}