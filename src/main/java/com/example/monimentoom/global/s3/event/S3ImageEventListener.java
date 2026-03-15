package com.example.monimentoom.global.s3.event;

import com.example.monimentoom.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageEventListener {
    private final S3Uploader s3Uploader;

    @Async("s3ImageTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteOldImage(S3ImageDeleteEvent event) {
        try {
            s3Uploader.deleteFile(event.imageUrl());
        }catch (Exception e) {
            // TODO : 추후 실패 URL 별도 저장하여 관리 기능 추가
            log.error("[S3 이미지 삭제 실패] 비동기 처리 중 오류 발생. imageUrl: {}, 원인: {}",
                    event.imageUrl(), e.getMessage(), e);
        }
    }
}
