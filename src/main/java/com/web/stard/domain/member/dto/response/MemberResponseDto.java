package com.web.stard.domain.member.dto.response;

import com.web.stard.domain.member.domain.Interest;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.InterestField;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MemberResponseDto {

    @Getter
    @Builder
    public static class SignupResultDto {
        private Long id;
        private LocalDateTime createdAt;

        public static SignupResultDto from(Member member){
            return SignupResultDto.builder()
                    .id(member.getId())
                    .createdAt(member.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class InfoDto {
        private String nickname; // 닉네임
        private String phone; // 전화번호
        private String city; // 시
        private String district; // 구
        private List<String> interests; // 관심분야

        public static InfoDto from(Member member, List<Interest> interestList){
            return InfoDto.builder()
                    .nickname(member.getNickname())
                    .phone(member.getPhone())
                    .city(member.getAddress().getCity())
                    .district(member.getAddress().getDistrict())
                    .interests(interestList.stream()
                            .map(Interest::getInterestField)
                            .map(InterestField::name)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EditNicknameResponseDto {
        private String nickname;
        private String message;

        public static EditNicknameResponseDto from(String nickname) {
            return EditNicknameResponseDto.builder()
                    .nickname(nickname)
                    .message("닉네임이 성공적으로 변경되었습니다.")
                    .build();
        }
    }

    @Getter
    @Builder
    public static class EditPhoneResponseDto {
        private String phone;
        private String message;

        public static EditPhoneResponseDto from(String phone) {
            return EditPhoneResponseDto.builder()
                    .phone(phone)
                    .message("전화번호가 성공적으로 변경되었습니다.")
                    .build();
        }
    }

}
