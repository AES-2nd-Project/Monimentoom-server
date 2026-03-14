package com.example.monimentoom.global.oauth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KakaoSignupRequest(
        @NotNull(message = "kakaoId는 필수값입니다.") Long kakaoId,              // 1단계에서 받은 kakaoId
        @NotBlank(message = "닉네임 필수입니다.") String nickname, // 사용자가 입력한 닉네임
        @NotBlank(message = "이메일은 필수입니다.") String email     // 사용자가 입력한 이메일
) {}
