package com.web.stard.domain.teamBlog.api;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.post.domain.dto.request.PostRequestDto;
import com.web.stard.domain.teamBlog.domain.dto.response.StudyPostResponseDto;
import com.web.stard.domain.teamBlog.service.StudyPostService;
import com.web.stard.global.domain.CurrentMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "studies-posts", description = "스터디 팀블로그 - 게시판 관련 API")
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

    @Operation(summary = "스터디 팀블로그 게시글 수정", description = "파일은 최대 5개까지 업로드 가능합니다. \n\n" +
            "삭제할 파일의 id를 넘겨주세요.")
    @PutMapping(value = "/{studyPostId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudyPostResponseDto.StudyPostDto> updateStudyPost(@CurrentMember Member member,
                                                                             @PathVariable(name = "studyId") Long studyId,
                                                                             @PathVariable(name = "studyPostId") Long studyPostId,
                                                                             @RequestPart(value = "file", required = false) @Size(max = 5, message = "파일은 최대 {max}개까지 업로드할 수 있습니다.") List<MultipartFile> files,
                                                                             @Valid @RequestPart(name = "requestDto") PostRequestDto.UpdateStudyPostDto requestDto) {
        return ResponseEntity.ok(studyPostService.updateStudyPost(studyId, studyPostId, files, requestDto, member));
    }


    @Operation(summary = "스터디 팀블로그 게시글 삭제")
    @DeleteMapping("/{studyPostId}")
    public ResponseEntity<Long> deleteStudyPost(@CurrentMember Member member,
                                                @PathVariable(name = "studyId") Long studyId,
                                                @PathVariable(name = "studyPostId") Long studyPostId) {
        return ResponseEntity.ok(studyPostService.deleteStudyPost(studyId, studyPostId, member));
    }

    @Operation(summary = "스터디 팀블로그 게시글 상세조회")
    @GetMapping("/{studyPostId}")
    public ResponseEntity<StudyPostResponseDto.StudyPostDto> getStudyPostDetail(@CurrentMember Member member,
                                                                                @PathVariable(name = "studyId") Long studyId,
                                                                                @PathVariable(name = "studyPostId") Long studyPostId) {
        return ResponseEntity.ok(studyPostService.getStudyPostDetail(studyId, studyPostId, member));
    }

    @Operation(summary = "스터디 팀블로그 게시글 목록 조회")
    @GetMapping
    public ResponseEntity<StudyPostResponseDto.StudyPostListDto> getStudyPostList(@CurrentMember Member member,
                                                                                  @PathVariable(name = "studyId") Long studyId,
                                                                                  @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(studyPostService.getStudyPostList(studyId, member, page));
    }

    @Operation(summary = "스터디 팀블로그 게시글 키워드 검색")
    @GetMapping("/search")
    public ResponseEntity<StudyPostResponseDto.StudyPostListDto> searchStudyPost(@CurrentMember Member member,
                                                                                 @PathVariable(name = "studyId") Long studyId,
                                                                                 @RequestParam(name = "keyword") String keyword,
                                                                                 @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return ResponseEntity.ok(studyPostService.searchStudyPost(studyId, keyword, member, page));
    }

    @Operation(summary = "스터디 팀블로그 게시글 파일 다운로드")
    @GetMapping("/download/{studyPostFileId}")
    public ResponseEntity<byte[]> downloadFile(@CurrentMember Member member,
                                               @PathVariable(name = "studyId") Long studyId,
                                               @PathVariable(name = "studyPostFileId") Long studyPostFileId) {
        return studyPostService.downloadFile(studyId, studyPostFileId, member);
    }
}
