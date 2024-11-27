package com.web.stard.domain.teamBlog.domain.dto.response;

import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.domain.entity.StudyPostFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class StudyPostResponseDto {

    @Getter
    @Builder
    public static class FileDto {
        @Schema(description = "팀블로그 게시글의 파일 아이디")
        private Long studyPostFileId;

        @Schema(description = "팀블로그 게시글 파일 명")
        private String fileName;

        @Schema(description = "팀블로그 게시글 파일 경로")
        private String fileUrl;

        public static FileDto of(StudyPostFile studyPostFile) {
            return FileDto.builder()
                    .studyPostFileId(studyPostFile.getId())
                    .fileName(studyPostFile.getFileName())
                    .fileUrl(studyPostFile.getFileUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StudyPostDto {
        @Schema(description = "팀블로그 게시글 아이디")
        private Long studyPostId;

        @Schema(description = "해당 스터디 아이디")
        private Long studyId;

        @Schema(description = "팀블로그 게시글 작성자")
        private String writer;

        @Schema(description = "팀블로그 게시글 프로필 이미지 경로")
        private String profileImg;

        @Schema(description = "팀블로그 게시글 작성 시간")
        private LocalDateTime createdAt;

        @Schema(description = "팀블로그 게시글 수정 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "팀블로그 게시글 제목")
        private String title;

        @Schema(description = "팀블로그 게시글 내용")
        private String content;

        @Schema(description = "팀블로그 게시글 파일 리스트")
        private List<FileDto> fileUrl;

        @Schema(description = "팀블로그 게시글 조회수")
        private int hit;

        @Schema(description = "팀블로그 게시글 스크랩 개수")
        private int scrapCount;

        @Schema(description = "팀블로그 게시글 작성자 여부")
        private boolean isAuthor;

        @Schema(description = "팀블로그 게시글 스크랩 여부")
        private boolean existsScrap;

        public static StudyPostDto from(StudyPost studyPost, int scrapCount, boolean isAuthor, boolean existsScrap) {
            List<FileDto> file = (studyPost.getFiles() != null) ? studyPost.getFiles().stream().map(FileDto::of).toList() : null;

            return StudyPostDto.builder()
                    .studyPostId(studyPost.getId())
                    .studyId(studyPost.getStudy().getId())
                    .writer(studyPost.getStudyMember().getMember().getNickname())
                    .profileImg(studyPost.getStudyMember().getMember().getProfile().getImgUrl())
                    .createdAt(studyPost.getCreatedAt())
                    .updatedAt(studyPost.getUpdatedAt())
                    .title(studyPost.getTitle())
                    .content(studyPost.getContent())
                    .fileUrl(file)
                    .hit(studyPost.getHit())
                    .scrapCount(scrapCount)
                    .isAuthor(isAuthor)
                    .existsScrap(existsScrap)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StudyPostItem {
        @Schema(description = "팀블로그 게시글 아이디")
        private Long studyPostId;

        @Schema(description = "팀블로그 게시글 작성자")
        private String writer;

        @Schema(description = "팀블로그 게시글 프로필 이미지 경로")
        private String profileImg;

        @Schema(description = "팀블로그 게시글 작성 시간")
        private LocalDateTime createdAt;

        @Schema(description = "팀블로그 게시글 수정 시간")
        private LocalDateTime updatedAt;

        @Schema(description = "팀블로그 게시글 제목")
        private String title;

        @Schema(description = "팀블로그 게시글 조회수")
        private int hit;

        @Schema(description = "팀블로그 게시글 스크랩 개수")
        private int scrapCount;

        @Schema(description = "팀블로그 게시글 파일 수 (파일 존재 여부 확인)")
        private int totalFiles;

        @Schema(description = "팀블로그 게시글 스크랩 여부")
        private boolean existsScrap;

        public static StudyPostItem of (StudyPost studyPost, int scrapCount, boolean existsScrap) {
            int totalFiles = (studyPost.getFiles() != null) ? studyPost.getFiles().size() : 0;

            return StudyPostItem.builder()
                    .studyPostId(studyPost.getId())
                    .writer(studyPost.getStudyMember().getMember().getNickname())
                    .profileImg(studyPost.getStudyMember().getMember().getProfile().getImgUrl())
                    .createdAt(studyPost.getCreatedAt())
                    .updatedAt(studyPost.getUpdatedAt())
                    .title(studyPost.getTitle())
                    .hit(studyPost.getHit())
                    .scrapCount(scrapCount)
                    .totalFiles(totalFiles)
                    .existsScrap(existsScrap)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class StudyPostListDto {
        @Schema(description = "해당 스터디 아이디")
        private Long studyId;

        @Schema(description = "팀블로그 게시글 목록")
        private List<StudyPostItem> items;

        @Schema(description = "현재 페이지")
        private int currentPage;

        @Schema(description = "전체 페이지 수")
        private int totalPages;

        @Schema(description = "마지막 페이지 여부")
        private boolean isLast;

        public static StudyPostListDto of (Long studyId, Page<StudyPost> studyPosts, List<StudyPostItem> itemDtos) {
            return StudyPostListDto.builder()
                    .studyId(studyId)
                    .items(itemDtos)
                    .currentPage(studyPosts.getNumber() + 1)
                    .totalPages(studyPosts.getTotalPages())
                    .isLast(studyPosts.isLast())
                    .build();
        }
    }
}
