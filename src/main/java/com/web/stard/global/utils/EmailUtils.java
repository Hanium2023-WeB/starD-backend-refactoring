package com.web.stard.global.utils;

import com.web.stard.global.exception.CustomException;
import com.web.stard.global.exception.error.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailUtils {

    private final JavaMailSender mailSender;
    private final RedisUtils redisUtils;

    @Value("${spring.mail.username}")
    private String from;


    public void send(String toMail) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            String authCode = createKey();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(toMail);
            helper.setSubject("StarD 인증 코드");
            helper.setText(this.createEmailContent(authCode), true);
            mailSender.send(message);

            // redis에 3분 동안 이메일과 인증 코드 저장
            redisUtils.setData(toMail, authCode, 180000L);

        } catch (MessagingException e) {
            log.error(e.getMessage(), e);       // TODO 커스텀 에러로 변경
        }
    }

    private String createKey() throws Exception {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e.getMessage());
        }
    }

    public String createEmailContent(String authCode) {
        return "이메일을 인증하기 위한 절차입니다." +
                "<br><br>" +
                "인증 번호는 " + authCode + "입니다." +
                "<br>" +
                "회원 가입 폼에 해당 번호를 입력해주세요.";
    }

    public void validAuthCode(String email, String code) throws Exception {
        String redisCode = redisUtils.getData(email);
        if (Objects.isNull(redisCode) && !Objects.equals(redisCode, code)) {
            throw new CustomException(ErrorCode.INVALID_AUTH_CODE);
        }
    }

}
