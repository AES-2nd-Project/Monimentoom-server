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
     * - 기존 유저: token + nickname + email 반환
     * - 신규 유저: signupToken 반환 -> /oauth/kakao/signup 호출 필요
     **/
    @PostMapping("/kakao")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@Valid @RequestBody KakaoSocialLoginRequest request) {
        KakaoLoginResponse response = kakaoOAuthService.kakaoLogin(request.code());
        if (response.token() != null) {
            return ResponseEntity.ok()
                    // TODO: 헤더 뺄지말지 결정
                    .header("Authorization", "Bearer " + response.token())
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 2단계: 닉네임 입력 후 최종 회원가입
     * - 응답: JWT token, nickname, email 반환
     */
    @PostMapping("/kakao/signup")
    public ResponseEntity<SignupResponse> kakaoSignup(@Valid @RequestBody KakaoSignupRequest request) {
        SignupResponse response = kakaoOAuthService.kakaoSignup(request);
        if (response.token() != null) {
            ResponseEntity.ok(response);
        }
    }

    // 로컬 테스트용
    @GetMapping("/kakao")
    public ResponseEntity<KakaoLoginResponse> kakaoLoginCallBack(@Valid @RequestParam String code) {
        KakaoLoginResponse response = kakaoOAuthService.kakaoLogin(code);
        if (response.token() != null) {
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + response.token())
                    .body(response);
        }
        return ResponseEntity.ok(response);
    }
}
