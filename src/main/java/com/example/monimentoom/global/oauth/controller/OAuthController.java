package com.example.monimentoom.global.oauth.controller;

import com.example.monimentoom.global.oauth.dto.KakaoLoginResponse;
import com.example.monimentoom.global.oauth.dto.KakaoSignupRequest;
import com.example.monimentoom.global.oauth.dto.KakaoSocialLoginRequest;
import com.example.monimentoom.global.oauth.dto.SignupResponse;
import com.example.monimentoom.global.oauth.service.KakaoOAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 1단계: 카카오 인가 코드 -> 로그인 or 신규 유저 판별
     * - 기존 유저: token + nickname 반환
     * - 신규 유저: signupToken 반환 -> /oauth/kakao/signup 호출 필요
     **/
    @PostMapping("/kakao")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@Valid @RequestBody KakaoSocialLoginRequest request) {
        KakaoLoginResponse response = kakaoOAuthService.kakaoLogin(request.code());
        return ResponseEntity.ok().body(response);
    }

    /**
     * 2단계: 닉네임 입력 후 최종 회원가입
     * - 응답: JWT token, nickname 반환
     */
    @PostMapping("/kakao/signup")
    public ResponseEntity<SignupResponse> kakaoSignup(@Valid @RequestBody KakaoSignupRequest request) {
        SignupResponse response = kakaoOAuthService.kakaoSignup(request);
        return ResponseEntity.ok(response);
    }
}
