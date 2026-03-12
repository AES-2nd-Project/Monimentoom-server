package com.example.monimentoom.global.s3.dto;

public record PresignedUrlResponse(
        String presignedUrl,  // 클라이언트가 S3에 PUT 요청할 URL
        String imageUrl,      // DB에 저장할 실제 이미지 URL
        String contentType    // PUT 요청 시 Content-Type 헤더에 반드시 이 값 사용
) {
}
