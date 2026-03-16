package com.example.monimentoom.global.oauth.dto;

/** 서버 내부용 dto */
public record SignupResult(
        SignupResponse response,
        String refreshToken
) {}
