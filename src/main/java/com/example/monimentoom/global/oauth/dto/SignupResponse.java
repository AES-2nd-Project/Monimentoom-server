package com.example.monimentoom.global.oauth.dto;

public record SignupResponse(
        String token,
        String nickname,
        String email
) {
}
