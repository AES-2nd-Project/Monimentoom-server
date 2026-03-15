package com.example.monimentoom.global.oauth.client;

import com.example.monimentoom.global.oauth.dto.KakaoUserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

// Feign 클라이언트: 카카오 API 서버에서는 사용자 정보를 가져오는 인터페이스
// kauth(인증 서버)와 kapi(API 서버)는 주소가 달라서 따로 만들어야 함
@FeignClient(name = "kakaoApiClient", url = "https://kapi.kakao.com")
public interface KakaoApiClient {
    // GET https://kapi.kakao.com/v2/user/me로 요청
    // Authorization 헤더에 "Bearer {액세스 토큰}"을 담아서 보내면
    // 카카오가 해당 유저의 정보를 돌려줌
    @GetMapping("/v2/user/me")
    KakaoUserInfoResponse getUserInfo(
            @RequestHeader("Authorization") String accessToken // "Bearer {액세스토큰}" 형태
    );
}