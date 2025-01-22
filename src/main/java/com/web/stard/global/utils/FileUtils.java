package com.web.stard.global.utils;

import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUtils {

    @Value("${file.path.profile}")
    private String profilePath;

    @Value("${file.path.chatPath}")
    private String chatPath;

    @Value("${file.path.studyPost}")
    private String studyPostPath;

    @Value("${file.path.window-root}")
    private String windowRoot;

    public String getUploadRootPath() {
        // os 가져오기
        String os = System.getProperty("os.name").toLowerCase();
        // user home 디렉토리 가져오기
        String userHome = System.getProperty("user.home");

        if (os.contains("win")) {
            return windowRoot;
        } else if (os.contains("mac") || os.contains("nix") || os.contains("nux")) {
            return userHome + "/stard";
        } else {
            return "/stard";
        }
    }

    /**
     * 디렉토리 생성
     */
    @PostConstruct
    public void init() {
        File folder = new File(getUploadRootPath() + profilePath);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        folder = new File(getUploadRootPath() + studyPostPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        folder = new File(getUploadRootPath() + chatPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * 단일 파일 업로드
     *
     * @param keyName
     * @param file
     * @return
     */
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

        try {
            // 파일 저장 경로와 파일명으로 파일 객체 생성
            File saveFile = new File(getUploadRootPath(), keyName + extension);

            // 업로드한 파일 데이터를 지정한 경로에 저장
            file.transferTo(saveFile);

            return keyName + extension;
        } catch (IOException e) {
            log.error("File upload failed : {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.UPLOAD_FAILED);
        }
    }


    /**
     * 다중 파일 업로드
     *
     * @param keyNames
     * @param files
     * @return
     */
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

    /**
     * 단일 파일 삭제
     *
     * @param fileUrl
     */
    public void deleteFile(String fileUrl) {
        try {
            File file = new File(getUploadRootPath() + fileUrl);

            if (file.exists()) {
                file.delete();
            } else {
                throw new FileNotFoundException(fileUrl);
            }
        } catch (Exception e) {
            log.error("File delete failed: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.DELETE_FAILED);
        }
    }

    /**
     * 다중 파일 삭제
     *
     * @param fileUrls
     */
    public void deleteFiles(List<String> fileUrls) {
        for (String fileUrl : fileUrls) {
            deleteFile(fileUrl);
        }
    }

    // profile 디렉토리
    public String generateProfileKeyName(UUID uuid) {
        return profilePath + '/' + uuid.toString();
    }

    // chat 디렉토리
    public String generateChatKeyName(UUID uuid) {
        return chatPath + '/' + uuid.toString();
    }

    // studyPost 디렉토리
    public String generateStudyPostKeyName(UUID uuid) {
        return studyPostPath + '/' + uuid.toString();
    }

    /**
     * 단일 파일 다운로드
     *
     * @param fileUrl 파일 저장 절대 경로
     * @return
     */
    public InputStream downloadFile(String fileUrl) {

        try {
            Resource resource = new UrlResource(fileUrl);

            // 파일이 존재하고 읽기 가능한지 확인
            if (!resource.exists() || !resource.isReadable()) {
                throw new CustomException(ErrorCode.DOWNLOAD_FAILED);
            }

            return resource.getInputStream();
        } catch (IOException e) {
            log.error("Fild download failed: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.DOWNLOAD_FAILED);
        }
    }
}
