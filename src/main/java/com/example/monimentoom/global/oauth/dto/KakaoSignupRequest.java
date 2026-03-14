package com.example.monimentoom.global.oauth.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoSignupRequest(
        Long kakaoId,              // 1단계에서 받은 kakaoId
        @NotBlank String nickname, // 사용자가 입력한 닉네임
        @NotBlank String email     // 사용자가 입력한 이메일
) {}
