package com.example.monimentoom.global.auth.scheduler;

import com.example.monimentoom.global.auth.repository.RefreshTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupScheduler {
    private final RefreshTokenJpaRepository jpa;
    /** 매일 새벽 3 시에 만료되었거나 폐기된 행 일괄 삭제 */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanUp() {
        jpa.deleteExpiredAndRevoked(LocalDateTime.now());
        log.info("리프레시 토큰 만료/폐기 행 정리 완료: {}", LocalDateTime.now());
    }
}