package com.example.monimentoom.global.auth.repository;

import com.example.monimentoom.global.auth.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RdbRefreshTokenRepository implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpa;

    @Override
    @Transactional
    public void save(Long userId, String token, String deviceId, long ttlSeconds) {
        // 1 기기 1 토큰 정책: 기존 토큰 먼저 폐기
        jpa.revokeByUserDevice(userId, deviceId);
        jpa.save(RefreshToken.builder()
                .userId(userId).token(token).deviceId(deviceId)
                .expiresAt(LocalDateTime.now().plusSeconds(ttlSeconds))
                .build());
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<String> find(Long userId, String deviceId) {
        return jpa.findValidToken(userId, deviceId, LocalDateTime.now())
                .map(RefreshToken::getToken);
    }
    @Override
    @Transactional
    public void revoke(Long userId, String deviceId) {
        jpa.revokeByUserDevice(userId, deviceId);
    }
    @Override
    @Transactional
    public void revokeAll(Long userId) {
        jpa.revokeAllByUserId(userId);
    }

    @Override
    @Transactional
    public boolean rotate(Long userId, String deviceId,
                          String oldToken, String newToken, long ttlSeconds) {
        int updated = jpa.rotateToken(
                userId,
                deviceId,
                oldToken,
                newToken,
                LocalDateTime.now().plusSeconds(ttlSeconds),
                LocalDateTime.now()
        );
        return updated == 1;  // 1행 교체 성공이면 true
    }
}
