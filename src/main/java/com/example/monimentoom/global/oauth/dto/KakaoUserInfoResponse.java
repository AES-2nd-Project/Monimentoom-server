package com.example.monimentoom.global.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

// 카카오 사용자 정보 API 응답 - id만 사용 (닉네임은 별도 회원가입에서 입력받음)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserInfoResponse(
        Long id  // 카카오 고유 유저 ID
) {}
