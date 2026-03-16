package com.example.monimentoom.global.auth.repository;

import java.util.Optional;

public interface RefreshTokenRepository {
    /** 저장. 같은 userId+deviceId 기존 토큰 먼저 폐기 후 저장 */
    void save(Long userId, String token, String deviceId, long ttlSeconds);
    /** 유효한 토큰 조회. 없거나 만료/폐기면 Optional.empty() */
    Optional<String> find(Long userId, String deviceId);
    /** 단일 기기 로그아웃 */
    void revoke(Long userId, String deviceId);
    /** 전체 기기 로그아웃 */
    void revokeAll(Long userId);
}
