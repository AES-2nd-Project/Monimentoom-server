package com.example.monimentoom.global.oauth.dto;

public record KakaoSocialLoginRequest(
        String code // 카카오가 발급한 인가 코드
) {
}