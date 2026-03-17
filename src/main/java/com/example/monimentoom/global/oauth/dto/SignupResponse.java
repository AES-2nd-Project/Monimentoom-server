package com.example.monimentoom.global.oauth.dto;

public record SignupResponse(
        String token,
        Long userId,
        String nickname,
        String profileImageUrl,
        String description
) {
}
