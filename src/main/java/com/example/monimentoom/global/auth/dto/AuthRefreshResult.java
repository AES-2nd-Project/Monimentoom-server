package com.example.monimentoom.global.auth.dto;

public record AuthRefreshResult(
        String accessToken,
        String refreshToken  // 컨트롤러에서 쿠키에 담을 것, 바디 미노출
) {}
