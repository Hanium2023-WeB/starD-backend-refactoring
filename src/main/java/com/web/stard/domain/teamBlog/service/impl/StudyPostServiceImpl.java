package com.web.stard.domain.teamBlog.service.impl;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.post.domain.enums.PostType;
import com.web.stard.domain.study.domain.entity.Study;
import com.web.stard.domain.study.domain.entity.StudyMember;
import com.web.stard.domain.study.repository.StudyMemberRepository;
import com.web.stard.domain.study.service.StudyService;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.domain.entity.StudyPostFile;
import com.web.stard.domain.teamBlog.repository.StudyPostFileRepository;
import com.web.stard.domain.teamBlog.repository.StudyPostRepository;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import com.web.stard.global.config.aws.S3Manager;
import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyPostServiceImpl implements StudyPostService {

    private final StudyPostRepository studyPostRepository;
    private final StudyService studyService;
    private final StudyMemberRepository studyMemberRepository;
    private final S3Manager s3Manager;
    private final StudyPostFileRepository studyPostFileRepository;


    /**
     * 회원가입
     *
     * @param studyId 해당 study 고유 id
     * @param member 로그인 회원
     * @param files 다중 파일
     * @param requestDto title 제목, content 내용
     *
     * @return StudyPostDto
     */
    @Transactional
    @Override
    public StudyPostResponseDto.StudyPostDto createStudyPost(Long studyId, List<MultipartFile> files, PostRequestDto.CreatePostDto requestDto, Member member) {
        Study study = studyService.findById(studyId);
        studyService.isStudyInProgress(study);
        studyService.isStudyMember(study, member);

        StudyMember studyMember = studyMemberRepository.findByStudyAndMember(study, member)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDY_MEMBER_NOT_FOUND));

        StudyPost studyPost = StudyPost.builder()
                .study(study)
                .studyMember(studyMember)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .postType(PostType.STUDYPOST)
                .hit(0)
                .build();

        studyPost = studyPostRepository.save(studyPost);

        // 다중 파일 저장
        List<StudyPostFile> studyPostFiles = null;

        if (files != null && !files.isEmpty()) {
            List<String> keyNames = new ArrayList<>();
            List<String> fileUrls;
            studyPostFiles = new ArrayList<>();

            for (MultipartFile file : files) {
                UUID uuid = UUID.randomUUID();
                keyNames.add(s3Manager.generateStudyPostKeyName(uuid));
            }

            fileUrls = s3Manager.uploadFiles(keyNames, files);

            for (int i = 0; i < fileUrls.size(); i++) {
                MultipartFile file = files.get(i);
                String originalFileName = file.getOriginalFilename();

                StudyPostFile studyPostFile = StudyPostFile.builder()
                        .studyPost(studyPost)
                        .fileName(originalFileName != null ? originalFileName : keyNames.get(i))
                        .fileUrl(fileUrls.get(i))
                        .build();

                studyPostFiles.add(studyPostFile);
            }

            studyPostFileRepository.saveAll(studyPostFiles);
        }

        return StudyPostResponseDto.StudyPostDto.from(studyPost, studyPostFiles);
    }
}
