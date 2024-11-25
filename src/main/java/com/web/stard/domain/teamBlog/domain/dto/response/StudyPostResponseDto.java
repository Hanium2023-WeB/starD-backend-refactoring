package com.web.stard.domain.teamBlog.domain.dto.response;

import com.web.stard.domain.teamBlog.domain.entity.StudyPost;
import com.web.stard.domain.teamBlog.domain.entity.StudyPostFile;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StudyPostResponseDto {

    @Getter
    @Builder
    public static class FileDto {
        private Long studyPostFileId;
        private String fileName;
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
        private Long studyPostId;
        private Long studyId;
        private String writer;
        private String profileImg;
        private String title;
        private String content;
        private List<FileDto> fileUrl;
        private int hit;
        private int scrapCount;
        private boolean isAuthor;

        public static StudyPostDto from(StudyPost studyPost, int scrapCount, boolean isAuthor) {
            List<FileDto> file = (studyPost.getFiles() != null) ? studyPost.getFiles().stream().map(FileDto::of).toList() : null;

            return StudyPostDto.builder()
                    .studyPostId(studyPost.getId())
                    .studyId(studyPost.getStudy().getId())
                    .writer(studyPost.getStudyMember().getMember().getNickname())
                    .profileImg(studyPost.getStudyMember().getMember().getProfile().getImgUrl())
                    .title(studyPost.getTitle())
                    .content(studyPost.getContent())
                    .fileUrl(file)
                    .hit(studyPost.getHit())
                    .scrapCount(scrapCount)
                    .isAuthor(isAuthor)
                    .build();
        }
    }
}
