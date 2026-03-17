package com.example.monimentoom.global.oauth.service;

import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.global.auth.repository.RefreshTokenRepository;
import com.example.monimentoom.global.oauth.dto.*;
import com.example.monimentoom.domain.user.repository.UserRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.oauth.client.KakaoApiClient;
import com.example.monimentoom.global.oauth.client.KakaoAuthClient;
import com.example.monimentoom.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {
    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client-id:}")
    private String clientId;
    @Value("${kakao.redirect-uri:}")
    private String redirectUri;
    @Value("${kakao.client-secret:}")
    private String clientSecret;

    /**
     * 1단계: 카카오 인가코드 -> 카카오 ID 확인
     * - 기존 유저: JWT 바로 발급
     * - 신규 유저: kakaoId만 반환 (닉네임 입력 필요)
     */
    public KakaoLoginResult kakaoLogin(String code) {
        log.info("kakaoLogin - redirectUri={}", redirectUri);

        // 1. 인가코드 -> 카카오 액세스 토큰
        KakaoAccessTokenResponse tokenResponse = kakaoAuthClient.getAccessToken(
                "authorization_code", clientId, redirectUri, code, clientSecret
        );

        // 2. 카카오 액세스 토큰 -> 카카오 유저 ID
        KakaoUserInfoResponse userInfo = kakaoApiClient.getUserInfo(
                "Bearer " + tokenResponse.accessToken()
        );
        Long kakaoId = userInfo.id();
        log.info("kakaoLogin - kakaoId={}", kakaoId);

        // 3. 기존 유저면 JWT 발급, 신규 유저면 signupToken(임시 토큰) 반환
        return userRepository.findByKakaoId(kakaoId)
                .map(user -> {
                    String at = jwtUtil.createToken(user.getId());
                    String rt = jwtUtil.createRefreshToken(user.getId());
                    refreshTokenRepository.save(
                            user.getId(), rt, "default",
                            jwtUtil.getRefreshTokenExpirySeconds());
                    return new KakaoLoginResult(
                            KakaoLoginResponse.ofExistingUser(at, user.getId(), user.getNickname(), user.getProfileImageUrl(), user.getDescription()),
                            rt   // 쿠키용
                    );
                })
                .orElse(new KakaoLoginResult(
                        KakaoLoginResponse.ofNewUser(jwtUtil.createSignupToken(kakaoId)),
                        null   // 신규 유저는 RT 없음
                ));
    }

    @Transactional
    public SignupResult kakaoSignup(KakaoSignupRequest request) {
        // signupToken 검증 → kakaoId 추출 (위조/만료 시 예외)
        Long kakaoId = jwtUtil.getKakaoIdFromSignupToken(request.signupToken());
        log.info("kakaoSignup - verified kakaoId={}", kakaoId);

        // kakaoId 중복 체크 (이미 가입된 카카오 계정)
        if (userRepository.existsByKakaoId(kakaoId)) {
            throw new CustomException(ErrorCode.DUPLICATE_KAKAO_USER);
        }
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 신규 유저 저장
        User newUser = User.builder()
                .kakaoId(kakaoId)
                .nickname(request.nickname())
                .profileImageUrl(request.profileImageUrl())
                .description(request.description())
                .build();
        userRepository.save(newUser);

        // 기본 방 생성
        Room defaultRoom = Room.builder()
                .name(newUser.getNickname() + "님의 첫 번째 방")
                .user(newUser)
                .build();
        roomRepository.save(defaultRoom);
        newUser.updateMainRoom(defaultRoom);

        String at = jwtUtil.createToken(newUser.getId());
        String rt = jwtUtil.createRefreshToken(newUser.getId());
        refreshTokenRepository.save(
                newUser.getId(), rt, "default",
                jwtUtil.getRefreshTokenExpirySeconds());

        log.info("kakaoSignup 완료 - userId={}, nickname={}", newUser.getId(), newUser.getNickname());
        return new SignupResult(
                new SignupResponse(at, newUser.getId(), newUser.getNickname(), newUser.getProfileImageUrl(), newUser.getDescription()),
                rt   // 쿠키용
        );
    }
}
