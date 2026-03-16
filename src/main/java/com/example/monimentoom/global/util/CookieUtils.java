package com.example.monimentoom.global.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {
    // 쿠키가 자동으로 첨부될 경로
    // /auth/refresh 에서만 읽으므로 /auth 로 제한
    // /oauth 경로에서는 쿠키를 읽지 않으므로 브라우저가 보내지 않아도 됨
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/auth";
    private static final long   REFRESH_TOKEN_MAX_AGE_DAYS = 7;

    private CookieUtils() {}   // 유틸 클래스 인스턴스화 방지용

    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)                              // 로컬 테스트 시 false
                .sameSite("Strict")
                .maxAge(Duration.ofDays(REFRESH_TOKEN_MAX_AGE_DAYS))
                .path(REFRESH_TOKEN_COOKIE_PATH)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(0)                                 // 즉시 만료
                .path(REFRESH_TOKEN_COOKIE_PATH)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}