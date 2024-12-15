package com.web.stard.domain.member.domain.dto.response;

import com.web.stard.domain.member.domain.entity.Interest;
import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class MemberResponseDto {

    @Getter
    @Builder
    public static class SignupResultDto {
        @Schema(description = "회원 고유 id")
        private Long memberId;

        @Schema(description = "계정 등록 시각")
        private LocalDateTime createdAt;

        public static SignupResultDto from(Member member){
            return SignupResultDto.builder()
                    .memberId(member.getId())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class AdditionalInfoResultDto {
        @Schema(description = "회원 고유 id")
        private Long memberId;

        @Schema(description = "회원 관심분야")
        private List<InterestField> interests;

        public static AdditionalInfoResultDto of(Member member){
            return AdditionalInfoResultDto.builder()
                    .memberId(member.getId())
                    .interests(member.getInterests().stream().map(Interest::getInterestField).toList())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class InfoDto {
        @Schema(description = "닉네임")
        private String nickname;

        @Schema(description = "관심분야")
        private List<String> interests;

        public static InfoDto of(Member member, List<Interest> interests){
            return InfoDto.builder()
                    .nickname(member.getNickname())
                    .interests(interests.stream().map(interest -> interest.getInterestField().getDescription()).toList())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EditNicknameResponseDto {
        @Schema(description = "닉네임")
        private String nickname;

        private String message;

        public static EditNicknameResponseDto of(String nickname) {
            return EditNicknameResponseDto.builder()
                    .nickname(nickname)
                    .message("닉네임이 성공적으로 변경되었습니다.")
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EditInterestResponseDto {
        @Schema(description = "관심분야")
        private List<String> interests;

        private String message;

        public static EditInterestResponseDto of(List<Interest> interestList) {
            return EditInterestResponseDto.builder()
                    .interests(interestList.stream().map(interest -> interest.getInterestField().getDescription()).toList())
                    .message("관심분야가 성공적으로 변경되었습니다.")
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ProfileImageResponseDto {
        @Schema(description = "프로필 이미지 url")
        private String imageUrl;

        public static ProfileImageResponseDto from(String imageUrl) {
            return ProfileImageResponseDto.builder()
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class DeleteDto {
        @Schema(description = "탈퇴한 회원 고유 id")
        private Long deletedMemberId;

        private String message;
    }
}
