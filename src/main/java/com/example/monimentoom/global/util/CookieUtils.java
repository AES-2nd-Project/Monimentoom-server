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

    // SameSite=None: 프론트엔드와 백엔드가 서로 다른 사이트(cross-site)이므로
    // 쿠키가 cross-site 요청에서도 전송되도록 허용. CSRF 보호는 /auth/refresh 컨트롤러에서
    // Origin 화이트리스트 + 커스텀 헤더(X-Refresh-Request) 검증으로 수행.
    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)                              // SameSite=None은 Secure 필수
                .sameSite("None")
                .maxAge(Duration.ofDays(REFRESH_TOKEN_MAX_AGE_DAYS))
                .path(REFRESH_TOKEN_COOKIE_PATH)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(0)                                 // 즉시 만료
                .path(REFRESH_TOKEN_COOKIE_PATH)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}