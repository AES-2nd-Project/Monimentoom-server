package com.example.monimentoom.global.oauth.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * 카카오 토큰 교환 응답 DTO
 * 카카오 응답은 snake_case(access_token)로 오기 때문에
 * @JsonNaming으로 자동 변환해줌 -> accessToken을 필드로 받을 수 있음
 * @param accessToken : 사용자 정보 조회할 때 씀
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoAccessTokenResponse(
        String accessToken // 카카오 액세스토큰 (사용자 정보 조회할 때 씀)
) {
}