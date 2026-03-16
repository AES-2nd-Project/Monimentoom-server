package com.example.monimentoom.global.oauth.dto;

public record SignupResponse(
        String token,
        String refreshToken,
        Long userId,
        String nickname,
        String email
) {
}
