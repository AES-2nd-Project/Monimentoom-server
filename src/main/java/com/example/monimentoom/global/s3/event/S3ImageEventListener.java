package com.example.monimentoom.global.s3.event;

import com.example.monimentoom.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class S3ImageEventListener {
    private final S3Uploader s3Uploader;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteOldImage(S3ImageDeleteEvent event) {
        s3Uploader.deleteFile(event.imageUrl());
    }
}
