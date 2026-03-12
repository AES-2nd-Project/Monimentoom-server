package com.example.monimentoom.global.s3;

import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Component
public class S3Uploader {

    @Value("${cloud.aws.s3.bucket:}")
    private String bucket;

    // 기본값(:) 설정 → 테스트 환경에서 키가 없어도 앱 컨텍스트 로드 가능
    @Value("${cloud.aws.credentials.access-key:}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key:}")
    private String secretKey;

    @Value("${cloud.aws.region.static:ap-northeast-2}")
    private String region;

    // S3 직접 조작용 (삭제 등) - 테스트 환경에서는 null
    private S3Client s3Client;

    // Presigned URL 서명용 - 테스트 환경에서는 null
    private StaticCredentialsProvider credentialsProvider;

    // S3Config 파일을 별도로 만들지 않고 PostContruct로 S3Client를 초기화하도록 함
    @PostConstruct
    public void init() {
        if (accessKey.isBlank() || secretKey.isBlank()) {
            log.warn("AWS 자격증명이 없습니다. S3 기능이 비활성화됩니다.");
            return;
        }
        credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
        s3Client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(region))
                .build();
    }

    /**
     * 파일 확장자로 Content-Type 자동 판단
     * 서명에 포함되므로 PUT 요청 시 이 값과 반드시 일치해야 함
     */
    // 파일 확장자로 Content-type 자동 판단
    // 서명에 포함되므로 PUT 요청 시 이 값과 반드시 일치해야 함(ex. .png, .jpg)
    private String resolveContentType(String fileName) {
        String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        return switch (ext) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png"         -> "image/png";
            case "webp"        -> "image/webp";
            case "gif"         -> "image/gif";
            default -> throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
        };
    }

    /**
     * 클라이언트가 S3에 직접 업로드할 수 있는 Presigned URL 발급 (유효시간 5분)
     * @param dirName: S3 저장 폴더명 (예: "goods", "profile")
     * @param originalfileName: 원본 파일명 -> UUID prefix 붙여서 중복 방지
     * @return
     */
    public PresignedUrlResult generatePresignedUrl(String dirName, String originalfileName) {
        String contentType = resolveContentType(originalfileName);
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + originalfileName;

        // try-with-resources로 S3Presigner 자동 close
        try (S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.of(region))
                .build()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(contentType) // 서명에 포함 → PUT 요청 시 Content-Type 헤더 필수
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();
            return new PresignedUrlResult(presignedUrl, extractImageUrl(presignedUrl), contentType);
        }
    }

    /**
     * @param presignedUrl: 클라이언트가 S3에 PUT 요청할 임시 URL
     * @param imageUrl: DB에 저장할 순수 S3 URL(쿼리 스트링 없게)
     * @param contentType: contentType: PUT 요청 시 Content-Type 헤더에 이 값 그대로 사용
     */
    public record PresignedUrlResult(String presignedUrl, String imageUrl, String contentType) {}

    // Presigned URL에서 쿼리스트링 제거 → DB 저장용 imageUrl 추출
    public String extractImageUrl(String presignedUrl) {
        return presignedUrl.split("\\?")[0];
    }

    // S3 파일 삭제 (AWS 키가 없으면 s3Client가 null → 삭제 안 하고 그냥 return)
    /**
     * S3 파일 삭제
     * @param imageUrl: DB에 저장된 순수 S3 URL: 버킷 이후 경로(key)만 추출해서 삭제 요청함
     * 테스트환경에서는 aws 키 없어서 @PostConstruct init()에서 s3Client = null -> 아무것도 안하고 넘어감(return)
     */
    public void deleteFile(String imageUrl) {
        if (s3Client == null) return;
        String key = imageUrl.substring(imageUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
        s3Client.deleteObject(builder -> builder.bucket(bucket).key(key).build());
        log.info("S3 파일 삭제 완료: {}", key);
    }
}
