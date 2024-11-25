package com.web.stard.domain.teamBlog.service;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyPostService {
    StudyPostResponseDto.StudyPostDto createStudyPost(Long studyId, List<MultipartFile> files, PostRequestDto.CreatePostDto requestDto, Member member);

    StudyPostResponseDto.StudyPostDto updateStudyPost(Long studyId, Long studyPostId, List<MultipartFile> files, PostRequestDto.UpdateStudyPostDto requestDto, Member member);

    Long deleteStudyPost(Long studyId, Long studyPostId, Member member);

    StudyPostResponseDto.StudyPostDto getStudyPostDetail(Long studyId, Long studyPostId, Member member);

    StudyPostResponseDto.StudyPostListDto getStudyPostList(Long studyId, Member member, int page);
}
