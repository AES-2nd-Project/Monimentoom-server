package com.example.monimentoom.global.oauth.dto;

/**
 * 카카오 로그인 1단계 응답
 * - 기존 유저: isNewUser=false, token 발급
 * - 신규 유저: isNewUser=true, signupToken 반환 (서버 서명 임시 토큰 5분 만료, /oauth/kakao/signup 호출 필요)
 */
public record KakaoLoginResponse(
        boolean isNewUser,
        String signupToken, // 신규 유저일 때만 사용 (kakaoId가 서명된 임시 토큰)
        String token        // 기존 유저일 때만 발급
) {
    public static KakaoLoginResponse ofExistingUser(String token) {
        return new KakaoLoginResponse(false, null, token);
    }

    public static KakaoLoginResponse ofNewUser(String signupToken) {
        return new KakaoLoginResponse(true, signupToken, null);
    }
}
