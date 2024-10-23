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
        private String city;
        private String district;
        private List<InterestField> interests;

        public static AdditionalInfoResultDto of(Member member){
            return AdditionalInfoResultDto.builder()
                    .memberId(member.getId())
                    .city(member.getAddress() != null ? member.getAddress().getCity() : null)
                    .district(member.getAddress() != null ? member.getAddress().getDistrict() : null)
                    .interests(member.getInterests().stream().map(Interest::getInterestField).toList())
                    .build();
        }
    }

}
