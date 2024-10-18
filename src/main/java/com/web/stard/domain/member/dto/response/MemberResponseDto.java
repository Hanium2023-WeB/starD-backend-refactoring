package com.web.stard.domain.member.dto.response;

import com.web.stard.domain.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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
}
