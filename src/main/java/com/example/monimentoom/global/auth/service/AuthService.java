package com.example.monimentoom.global.auth.service;

import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.auth.dto.TokenRefreshRequest;
import com.example.monimentoom.global.auth.dto.TokenRefreshResponse;
import com.example.monimentoom.global.auth.repository.RefreshTokenRepository;
import com.example.monimentoom.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String incoming = request.refreshToken();
        String deviceId = request.deviceId();
        // 1. JWT 서명·만료 검증 →userId 추출
        Long userId = jwtUtil.getUserIdFromRefreshToken(incoming);
        // 2. DB 에서 유효한 토큰 조회
        String stored = refreshTokenRepository.find(userId, deviceId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));
        // 3. RTR: 불일치 → 탈취 의심, 전체 기기 세션 폐기
        if (!stored.equals(incoming)) {
            log.warn("RT 재사용 감지, 전체 폐기: userId={}", userId);
            refreshTokenRepository.revokeAll(userId);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REUSED);
        }
        // 4. 새 토큰 쌍 발급 (save 가 내부적으로 기존 것 폐기 후 저장)
        String newAt = jwtUtil.createToken(userId);
        String newRt = jwtUtil.createRefreshToken(userId);
        refreshTokenRepository.save(userId, newRt, deviceId,
                jwtUtil.getRefreshTokenExpiration());
        return new TokenRefreshResponse(newAt, newRt);
    }

    @Transactional
    public void logout(Long userId, String deviceId) {
        refreshTokenRepository.revoke(userId, deviceId);
    }

    @Transactional
    public void logoutAll(Long userId) {
        refreshTokenRepository.revokeAll(userId);
    }
}
