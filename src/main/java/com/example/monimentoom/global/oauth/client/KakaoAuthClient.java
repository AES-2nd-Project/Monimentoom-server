package com.example.monimentoom.global.oauth.client;

import com.example.monimentoom.config.FeignConfig;
import com.example.monimentoom.global.oauth.dto.KakaoAccessTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoAuthClient",
        url = "https://kauth.kakao.com",
        configuration = FeignConfig.class
)
public interface KakaoAuthClient {
    // POST https://kauth.kakao.com/oauth/token으로 요청을 보냄
    // 인가코드(code)를 넘기면 카카오가 액세스토큰을 돌려줌
    @PostMapping(value = "/oauth/token", consumes = "application/x-www-form-urlencoded")
    KakaoAccessTokenResponse getAccessToken(
            @RequestParam("grant_type") String grantType, // 항상 "authorization_code"
            @RequestParam("client_id") String clientId, // 카카오 앱 REST API 키
            @RequestParam("redirect_uri") String redirectUri, // 카카오 콘솔에 등록한 redirect URI
            @RequestParam("code") String code, // 프론트에서 받은 인가코드
            @RequestParam("client_secret") String clientSecret
    );
}