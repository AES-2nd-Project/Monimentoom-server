package com.example.monimentoom.global.oauth.dto;

import jakarta.validation.constraints.NotBlank;

public record KakaoSocialLoginRequest(
        @NotBlank String code // 카카오가 발급한 인가 코드
) {
}