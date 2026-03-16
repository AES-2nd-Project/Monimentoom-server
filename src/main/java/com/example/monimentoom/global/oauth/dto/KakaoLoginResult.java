package com.example.monimentoom.global.oauth.dto;

/** 서버 내부용 DTO */
public record KakaoLoginResult(
        KakaoLoginResponse response,  // 바디에 담을 것
        String refreshToken           // 쿠키에 담을 것 — null이면 신규 유저
) {}
