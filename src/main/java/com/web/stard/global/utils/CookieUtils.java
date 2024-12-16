package com.web.stard.global.utils;

import com.web.stard.global.dto.TokenInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    private final String COOKIE_NAME = "refreshToken";

    public String getCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COOKIE_NAME)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public HttpServletResponse generateRefreshTokenCookie(HttpServletResponse response, TokenInfo tokenInfo) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, tokenInfo.getRefreshToken())
                .maxAge(604800)
                .path("/")
                .sameSite("None")
                .httpOnly(true)
                .secure(true)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return response;
    }

    public HttpServletResponse deleteRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, refreshToken)
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
        return response;
    }


}
