package com.web.stard.domain.member.dto.response;

import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class MemberResponseDto {

    @Getter
    @Builder
    public static class SignupResultDto {
        private Long memberId;
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
        private Long memberId;
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
        private String nickname; // 닉네임
        private List<InterestField> interests; // 관심분야

        public static InfoDto of(Member member){
            return InfoDto.builder()
                    .nickname(member.getNickname())
//                    .interests(member.getInterests().stream().map(Interest::getInterestField).toList())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EditNicknameResponseDto {
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
        private List<InterestField> interests;
        private String message;

        public static EditInterestResponseDto of(List<Interest> interestList) {
            return EditInterestResponseDto.builder()
                    .interests(interestList.stream().map(Interest::getInterestField).toList())
                    .message("관심분야가 성공적으로 변경되었습니다.")
                    .build();
        }
    }

}
