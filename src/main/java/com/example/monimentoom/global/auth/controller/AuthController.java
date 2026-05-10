package com.example.monimentoom.global.auth.controller;

import com.example.monimentoom.config.CorsConfig;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.auth.dto.AuthRefreshResult;
import com.example.monimentoom.global.auth.dto.TokenRefreshResponse;
import com.example.monimentoom.global.auth.service.AuthService;
import com.example.monimentoom.global.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 액세스 토큰 만료 상태로 호출
    // SameSite=None 쿠키를 사용하므로 CSRF 방어를 위해 Origin 화이트리스트와
    // 커스텀 헤더(X-Refresh-Request)를 검증한다. 커스텀 헤더는 CORS preflight를
    // 강제하므로 화이트리스트 외 origin에서는 사실상 호출 불가.
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestHeader(name = "Origin", required = false) String origin,
            @RequestHeader(name = "X-Refresh-Request", required = false) String refreshRequestHeader,
            HttpServletResponse response) {

        if (origin == null || !CorsConfig.ALLOWED_ORIGINS.contains(origin)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        if (refreshRequestHeader == null || refreshRequestHeader.isBlank()) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        AuthRefreshResult result = authService.refresh(refreshToken, "default");
        CookieUtils.addRefreshTokenCookie(response, result.refreshToken());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + result.accessToken())
                .body(new TokenRefreshResponse(result.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response) {

        authService.logout(userId, "default");
        CookieUtils.deleteRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }


    /** 현재 1기기 1계정이므로 사용하지 않음. */
    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response) {

        authService.logoutAll(userId);
        CookieUtils.deleteRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();
    }

}
