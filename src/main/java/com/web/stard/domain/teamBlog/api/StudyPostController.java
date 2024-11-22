package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/studies/{studyId}/study-posts")
@RequiredArgsConstructor
public class StudyPostController {

    private final StudyPostService studyPostService;

    @Operation(summary = "스터디 팀블로그 게시글 등록", description = "파일은 최대 5개까지 업로드 가능합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudyPostResponseDto.StudyPostDto> createStudyPost(@CurrentMember Member member,
                                                                             @PathVariable(name = "studyId") Long studyId,
                                                                             @RequestPart(value = "file", required = false) @Size(max = 5, message = "파일은 최대 {max}개까지 업로드할 수 있습니다.") List<MultipartFile> files,
                                                                             @Valid @RequestPart(name = "requestDto") PostRequestDto.CreatePostDto requestDto) {
        return ResponseEntity.ok(studyPostService.createStudyPost(studyId, files, requestDto, member));
    }
}
