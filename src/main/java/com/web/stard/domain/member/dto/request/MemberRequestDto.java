package com.web.stard.domain.member.dto.request;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDto {

    @Getter
    @Builder
    public static class SignupDto {

        @NotBlank(message = "이메일은 필수입니다.")
        @Schema(example = "user@naver.com", description = "회원가입 이메일")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입니다.")
        @Schema(example = "user123!", description = "회원가입 비밀번호")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 8 ~ 15자 영문, 숫자, 특수문자 조합이어야 합니다.")
        private String password;

        @Size(min = 2, message = "이름은 2자 이상이어야 합니다.")
        @Schema(example = "스타디", description = "회원 이름")
        @NotBlank(message = "이름은 필수 입니다.")
        private String name;

        @Size(min = 2, message = "닉네임은 2자 이상이어야 합니다.")
        @Schema(example = "스타", description = "회원 닉네임")
        @NotBlank(message = "닉네임은 필수 입니다.")
        private String nickname;

        @Size(min = 7, message = "전화번호는 7자 이상이어야 합니다.")
        @Schema(example = "010-1234-5678", description = "회원 전화번호")
        @NotBlank(message = "전화번호는 필수 입니다.")
        private String phone;

        public Member toEntity(String encodedPassword){
            return Member.builder()
                    .email(email)
                    .password(encodedPassword)
                    .name(name)
                    .nickname(nickname)
                    .phone(phone)
                    .role(Role.USER)
                    .build();
        }

    }
}
