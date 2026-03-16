package com.example.monimentoom.global.oauth.controller;

import com.example.monimentoom.global.oauth.dto.*;
import com.example.monimentoom.global.oauth.service.KakaoOAuthService;
import com.example.monimentoom.global.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final KakaoOAuthService kakaoOAuthService;

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)          // 로컬 테스트 시 false로 변경
                .sameSite("Strict")
                .maxAge(Duration.ofDays(7))
                .path("/auth")         // /auth 요청에만 자동 첨부
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    /**
     * 1단계: 카카오 인가 코드 -> 로그인 or 신규 유저 판별
     * - 기존 유저: token + nickname 반환
     * - 신규 유저: signupToken 반환 -> /oauth/kakao/signup 호출 필요
     **/
    @PostMapping("/kakao")
    public ResponseEntity<KakaoLoginResponse> kakaoLogin(
            @Valid @RequestBody KakaoSocialLoginRequest request,
            HttpServletResponse response) {

        KakaoLoginResult result = kakaoOAuthService.kakaoLogin(request.code());

        if (result.response().token() != null) {
            CookieUtils.addRefreshTokenCookie(response, result.refreshToken());
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + result.response().token())
                    .body(result.response());
        }
        return ResponseEntity.ok(result.response());
    }
    /**
     * 2단계: 닉네임 입력 후 최종 회원가입
     * - 응답: JWT token, nickname 반환
     */
    @PostMapping("/kakao/signup")
    public ResponseEntity<SignupResponse> kakaoSignup(
            @Valid @RequestBody KakaoSignupRequest request,
            HttpServletResponse response) {

        SignupResult result = kakaoOAuthService.kakaoSignup(request);
        CookieUtils.addRefreshTokenCookie(response, result.refreshToken());

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + result.response().token())
                .body(result.response());
    }

    // 로컬 테스트용
    @GetMapping("/kakao")
    public ResponseEntity<KakaoLoginResponse> kakaoLoginCallBack(
            @RequestParam String code,
            HttpServletResponse response) {

        KakaoLoginResult result = kakaoOAuthService.kakaoLogin(code);

        if (result.response().token() != null) {
            CookieUtils.addRefreshTokenCookie(response, result.refreshToken());
            return ResponseEntity.ok()
                    .header("Authorization", "Bearer " + result.response().token())
                    .body(result.response());
        }
        return ResponseEntity.ok(result.response());
    }
}
