package com.example.monimentoom.global.oauth.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoSignupRequest(
        @NotBlank String signupToken, // 1단계에서 받은 서버 서명 임시 토큰
        @NotBlank String nickname,
        String profileImageUrl,       // 카카오 프로필 이미지 URL (선택)
        String description            // 한줄 소개 (선택)
) {}
