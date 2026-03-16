package com.example.monimentoom.global.auth.controller;

import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.auth.dto.AuthRefreshResult;
import com.example.monimentoom.global.auth.dto.TokenRefreshResponse;
import com.example.monimentoom.global.auth.service.AuthService;
import com.example.monimentoom.global.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 액세스 토큰 만료 상태로 호출
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

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
