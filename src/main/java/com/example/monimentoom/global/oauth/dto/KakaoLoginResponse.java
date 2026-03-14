package com.example.monimentoom.global.oauth.dto;


/**
 * 프론트에서 인가코드 받는 응답 DTO(카카오 로그인 1단계)
 * - 기존 유저: isNewUser=false, token 발급
 * - 신규 유저: isNewUser=true, kakaoId 반환 (닉네임 입력 후 /oauth/kakao/signup 호출 필요)
 */
public record KakaoLoginResponse(
        boolean isNewUser,
        Long kakaoId,    // 신규 유저일 때만 사용, 기존 유저는 null
        String token     // 기존 유저일 때만 발급, 신규 유저는 null
) {
    public static KakaoLoginResponse ofExistingUser(String token) {
        return new KakaoLoginResponse(false, null, token);
    }

    public static KakaoLoginResponse ofNewUser(Long kakaoId) {
        return new KakaoLoginResponse(true, kakaoId, null);
    }
}
