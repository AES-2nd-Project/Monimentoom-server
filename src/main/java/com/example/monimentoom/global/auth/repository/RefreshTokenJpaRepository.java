package com.example.monimentoom.global.auth.repository;

import com.example.monimentoom.global.auth.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    /** 리프레쉬 토큰의 교체 수행 원자성 보장
     * UPDATE 성공 여부로 판단
     * */
    @Modifying
    @Query("""
    UPDATE RefreshToken r
    SET r.token = :newToken,
        r.expiresAt = :newExpiresAt
    WHERE r.userId = :userId
      AND r.deviceId = :deviceId
      AND r.token = :oldToken
      AND r.revoked = false
      AND r.expiresAt > :now
""")
    int rotateToken(
            @Param("userId") Long userId,
            @Param("deviceId") String deviceId,
            @Param("oldToken") String oldToken,
            @Param("newToken") String newToken,
            @Param("newExpiresAt") LocalDateTime newExpiresAt,
            @Param("now") LocalDateTime now
    );

    @Query("""
 SELECT r FROM RefreshToken r
 WHERE r.userId = :userId
 AND r.deviceId = :deviceId
 AND r.revoked = false
 AND r.expiresAt > :now
 """)
    Optional<RefreshToken> findValidToken(
            @Param("userId") Long userId,
            @Param("deviceId") String deviceId,
            @Param("now") LocalDateTime now
    );
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true" +
            " WHERE r.userId = :userId AND r.deviceId = :deviceId")
    void revokeByUserDevice(@Param("userId") Long userId,
                            @Param("deviceId") String deviceId);
    @Modifying
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.userId = :userId")
    void revokeAllByUserId(@Param("userId") Long userId);

    // 스케줄러 전용
    @Modifying
    @Query("DELETE FROM RefreshToken r" +
            " WHERE r.expiresAt < :now OR r.revoked = true")
    void deleteExpiredAndRevoked(@Param("now") LocalDateTime now);
}
