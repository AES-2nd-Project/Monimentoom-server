package com.example.monimentoom.global.oauth.service;

import com.example.monimentoom.domain.room.model.Room;
import com.example.monimentoom.domain.room.repository.RoomRepository;
import com.example.monimentoom.domain.user.model.User;
import com.example.monimentoom.domain.user.repository.UserRepository;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.oauth.client.KakaoApiClient;
import com.example.monimentoom.global.oauth.client.KakaoAuthClient;
import com.example.monimentoom.global.oauth.dto.KakaoAccessTokenResponse;
import com.example.monimentoom.global.oauth.dto.KakaoLoginResponse;
import com.example.monimentoom.global.oauth.dto.KakaoSignupRequest;
import com.example.monimentoom.global.oauth.dto.KakaoUserInfoResponse;
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
    @Transactional(readOnly = true)
    public KakaoLoginResponse kakaoLogin(String code) {
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

        // 3. 기존 유저면 JWT 발급, 신규 유저면 kakaoId 반환
        return userRepository.findByKakaoId(kakaoId)
                .map(user -> KakaoLoginResponse.ofExistingUser(jwtUtil.createToken(user.getId())))
                .orElse(KakaoLoginResponse.ofNewUser(kakaoId));
    }

    /**
     * 2단계: 닉네임과 이메일 입력 후 최종 회원가입 + JWT 발급
     */
    @Transactional
    public String kakaoSignup(KakaoSignupRequest request) {
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 신규 유저 저장
        User newUser = User.builder()
                .kakaoId(request.kakaoId())
                .nickname(request.nickname())
                .email(request.email())
                .build();
        userRepository.save(newUser);

        // 기본 방 생성 (일반 회원가입과 동일)
        Room defaultRoom = Room.builder()
                .name(newUser.getNickname() + "님의 첫 번째 방")
                .user(newUser)
                .build();
        roomRepository.save(defaultRoom);
        newUser.setMainRoom(defaultRoom);

        log.info("kakaoSignup 완료 - userId={}, nickname={}", newUser.getId(), newUser.getNickname());
        return jwtUtil.createToken(newUser.getId());
    }
}
