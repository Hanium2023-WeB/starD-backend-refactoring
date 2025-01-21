package com.web.stard.domain.member.domain.dto.request;

import com.web.stard.domain.member.domain.entity.Member;
import com.web.stard.domain.member.domain.entity.Profile;
import com.web.stard.domain.member.domain.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

public class MemberRequestDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignupDto {

        @NotBlank(message = "이메일은 필수입니다.")
        @Schema(example = "user@naver.com", description = "회원가입 이메일")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입니다.")
        @Schema(example = "user123!", description = "회원가입 비밀번호")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 8 ~ 15자 영문, 숫자, 특수문자 조합이어야 합니다.")
        private String password;

        @Size(min = 2, message = "닉네임은 2자 이상이어야 합니다.")
        @Schema(example = "스타", description = "회원 닉네임")
        @NotBlank(message = "닉네임은 필수 입니다.")
        private String nickname;

        @Builder
        public void Member(String email, String password, String nickname) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
        }

        public Member toEntity(String encodedPassword, Profile profile) {
            return Member.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname(nickname)
                    .role(Role.USER)
                    .profile(profile)
                    .build();
        }

    }

    public record SignInDto(

            @NotBlank(message = "이메일을 입력해주세요.")
            @Schema(example = "user@naver.com", description = "이메일")
            @Email(message = "이메일 형식에 맞지 않습니다.")
            String email,

            @NotBlank(message = "비밀번호을 입력해주세요.")
            @Schema(example = "user123!", description = "비밀번호")
            String password

    ) {

    }

    public record AuthCodeRequestDto(

            @NotBlank(message = "이메일을 입력해주세요.")
            @Schema(example = "user@naver.com", description = "이메일")
            @Email(message = "이메일 형식에 맞지 않습니다.")
            String email,

            @NotBlank(message = "인증 코드를 입력해주세요.")
            @Schema(example = "123456", description = "인증 코드")
            String authCode

    ) {

    }

    public record EmailRequestDto(
            @NotBlank(message = "이메일을 입력해주세요.")
            @Schema(example = "user@naver.com", description = "이메일")
            @Email(message = "이메일 형식에 맞지 않습니다.")
            String email
    ) {

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfoRequestDto {
        private Long memberId;
        private List<String> interests;
    }

    @Getter
    @Builder
    public static class EditPasswordDto {
        private String originPassword;

        @NotBlank(message = "비밀번호는 필수 입니다.")
        @Schema(example = "user123!", description = "회원가입 비밀번호")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호는 8 ~ 15자 영문, 숫자, 특수문자 조합이어야 합니다.")
        private String password;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EditNicknameDto {
        @Size(min = 2, message = "닉네임은 2자 이상이어야 합니다.")
        @Schema(example = "스타", description = "회원 닉네임")
        @NotBlank(message = "닉네임은 필수 입니다.")
        private String nickname;
    }

    public record EditIntroduceDto(
            @Schema(example = "안녕하세요!", description = "자기소개")
            String introduce
    ) {

    }


    public record EditProfileDto(

            @Schema(example = "안녕하세요!", description = "자기소개")
            @Size(max = 15, message = "자기소개는 최대 {max}자까지 입력 가능합니다.")
            String introduce
    ) {
    }


}
