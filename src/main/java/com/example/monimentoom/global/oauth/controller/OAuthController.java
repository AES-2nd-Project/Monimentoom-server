package com.example.monimentoom.global.oauth.controller;

import com.example.monimentoom.global.oauth.dto.KakaoLoginResponse;
import com.example.monimentoom.global.oauth.dto.KakaoSignupRequest;
import com.example.monimentoom.global.oauth.dto.KakaoSocialLoginRequest;
import com.example.monimentoom.global.oauth.service.KakaoOAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    /**
     * 1단계: 카카오 인가 코드 -> 로그인 or 신규 유저 판별
     * 응답:
     * - 기존 유저: {isNewUser: false, token: "Bearer ..."} + Authorization 헤더
     * - 신규 유저: {isNewUser: true, kakaoId: "12345..."} -> /oauth/kakao/signup 호출 필요
     * **/
    @PostMapping("/kakao")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(@Valid @RequestBody KakaoSocialLoginRequest request) {
        KakaoLoginResponse response = kakaoOAuthService.kakaoLogin(request.code());

        if (!response.isNewUser()) {
            // 기존 유저: Authorization 헤더 + body 모두 반환
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + response.token())
                    .body(response);
        }
        // 신규 유저: kakaoId만 반환, 프론트에서 닉네임 입력 후 /oauth/kakao/signup 호출
        return ResponseEntity.ok(response);
    }

    /**
     * 2단계: 닉네임 입력 후 최종 회원가입
     * 요청: { kakaoId: 12345, nickname: "홍길동" }
     * 응답: Authorization 헤더에 JWT
     */
    @PostMapping("/kakao/signup")
    public ResponseEntity<Void> kakaoSignup(@Valid @RequestBody KakaoSignupRequest request) {
        String token = kakaoOAuthService.kakaoSignup(request);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + token)
                .build();
    }

    // 로컬 테스트용 - 브라우저에서 카카오 redirect_uri를 백엔드로 설정했을 때 사용
    @GetMapping("/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLoginCallBack(@RequestParam String code) {
        KakaoLoginResponse response = kakaoOAuthService.kakaoLogin(code);
        if (!response.isNewUser()) {
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + response.token())
                    .body(Map.of("isNewUser", false, "token", response.token()));
        }
        return ResponseEntity.ok(Map.of("isNewUser", true, "kakaoId", response.kakaoId()));
    }
}
