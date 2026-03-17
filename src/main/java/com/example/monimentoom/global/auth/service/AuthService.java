package com.example.monimentoom.global.auth.service;

import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import com.example.monimentoom.global.auth.dto.AuthRefreshResult;
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
    public AuthRefreshResult refresh(String refreshToken, String deviceId) {

        // 1. JWT 서명·만료 검증
        Long userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);

        // 2. 새 토큰 미리 발급
        String newAt = jwtUtil.createToken(userId);
        String newRt = jwtUtil.createRefreshToken(userId);

        // 3. 원자성 보장 하는 교체
        boolean rotated = refreshTokenRepository.rotate(
                userId, deviceId,
                refreshToken,
                newRt,
                jwtUtil.getRefreshTokenExpirySeconds()
        );

        // 4. 교체 실패한 경우 전체 폐기
        if (!rotated) {
            log.warn("RT 재사용 또는 만료 감지, 전체 폐기: userId={}", userId);
            refreshTokenRepository.revokeAll(userId);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        return new AuthRefreshResult(newAt, newRt);
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
