package com.example.monimentoom.global.s3.controller;

import com.example.monimentoom.global.s3.S3Uploader;
import com.example.monimentoom.global.s3.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3/presigned-url/")
@RequiredArgsConstructor
public class S3Controller {
    private final S3Uploader s3Uploader;

    // 굿즈 이미지 업로드용 Presigned URL
    @GetMapping("/goods")
    public ResponseEntity<PresignedUrlResponse> getGoodsPresignedUrl(
            @RequestParam String fileName
    ) {
        S3Uploader.PresignedUrlResult result = s3Uploader.generatePresignedUrl("goods", fileName);
        return ResponseEntity.ok(new PresignedUrlResponse(result.presignedUrl(), result.imageUrl(), result.contentType()));
    }

    // 프로필 이미지 업로드용 Presigned URL
    @GetMapping("/profile")
    public ResponseEntity<PresignedUrlResponse> getProfilePresignedUrl(
            @RequestParam String fileName
    ) {
        S3Uploader.PresignedUrlResult result = s3Uploader.generatePresignedUrl("profile", fileName);
        return ResponseEntity.ok(new PresignedUrlResponse(result.presignedUrl(), result.imageUrl(), result.contentType()));
    }

    @GetMapping("/frame")
    public ResponseEntity<PresignedUrlResponse> getFramePresignedUrl(
            @RequestParam String fileName
    ) {
        S3Uploader.PresignedUrlResult result = s3Uploader.generatePresignedUrl("frame", fileName);
        return ResponseEntity.ok(new PresignedUrlResponse(result.presignedUrl(), result.imageUrl(), result.contentType()));
    }

    @GetMapping("/easel")
    public ResponseEntity<PresignedUrlResponse> getEaselPresignedUrl(
            @RequestParam String fileName
    ) {
        S3Uploader.PresignedUrlResult result = s3Uploader.generatePresignedUrl("easel", fileName);
        return ResponseEntity.ok(new PresignedUrlResponse(result.presignedUrl(), result.imageUrl(), result.contentType()));
    }
}
