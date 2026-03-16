package com.example.monimentoom.global.auth.controller;

import com.example.monimentoom.global.auth.dto.TokenRefreshRequest;
import com.example.monimentoom.global.auth.dto.TokenRefreshResponse;
import com.example.monimentoom.global.auth.service.AuthService;
import jakarta.validation.Valid;
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
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse res = authService.refresh(request);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + res.token())
                .body(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Long userId,
            @RequestParam String deviceId) {
        authService.logout(userId, deviceId);
        return ResponseEntity.noContent().build();
    }

    /** 현재 1기기 1계정이므로 사용하지 않음. */
    @PostMapping("/logout/all")
    public ResponseEntity<Void> logoutAll(
            @AuthenticationPrincipal Long userId) {
        authService.logoutAll(userId);
        return ResponseEntity.noContent().build();
    }
}
