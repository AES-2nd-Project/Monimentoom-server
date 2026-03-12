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

import java.net.URI;
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

    // Region.of() 에서 예외 방지 위해 기본값 설정
    @Value("${cloud.aws.region.static:ap-northeast-2}")
    private String region;

    // S3 직접 조작용 (삭제 등) - 테스트 환경에서는 null
    private S3Client s3Client;

    // Presigned URL 서명용 - 테스트 환경에서는 null
    private StaticCredentialsProvider credentialsProvider;

    // S3Config 파일을 별도로 만들지 않고 @PostContruct로 S3Client를 초기화하도록 함
    @PostConstruct
    public void init() {
        // 키가 없어도 경고만 하고 앱은 시작되게 함.
        // generatePresignedUrl를 실제로 호출할 때만 예외를 던짐.
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
     * @param originalFileName: 원본 파일명 -> UUID prefix 붙여서 중복 방지
     * @return
     */
    public PresignedUrlResult generatePresignedUrl(String dirName, String originalFileName) {
        if (credentialsProvider == null) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_UNAVAILABLE);
        }
        String contentType = resolveContentType(originalFileName);
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + originalFileName;

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

    /**
     * S3 파일 삭제
     * @param imageUrl: DB에 저장된 순수 S3 URL: 버킷 이후 경로(key)만 추출해서 삭제 요청함
     * 테스트환경에서는 aws 키 없어서 @PostConstruct init()에서 s3Client = null -> 아무것도 안하고 넘어감(return)
     */
    public void deleteFile(String imageUrl) {
        if (s3Client == null) return;

        String key;
        // url에서 host, 길이 검증
        try {
            URI uri = URI.create(imageUrl);
            String host = uri.getHost(); // e.g. bucket-name.s3.ap-northeast-2.amazonaws.com
            String path = uri.getPath(); // e.g. /goods/uuid_filename.jpg

            // 호스트 검증: 버킷명 + amazonaws.com 도메인인지 확인
            if (host == null || !host.endsWith(".amazonaws.com")) {
                log.warn("S3 삭제 건너뜀 - 기대하지 않는 호스트: {}", host);
                return;
            }

            // path 앞의 '/' 제거해 key 추출
            key = path.startsWith("/") ? path.substring(1) : path;

            if (key.isBlank()) {
                log.warn("S3 삭제 건너뜀 - key가 비어 있습니다. imageUrl={}", imageUrl);
                return;
            }
        } catch (IllegalArgumentException e) {
            log.warn("S3 삭제 건너뜀 - 유효하지 않은 URL 형식: {}", imageUrl, e);
            return;
        }

        s3Client.deleteObject(builder -> builder.bucket(bucket).key(key).build());
        log.info("S3 파일 삭제 완료: {}", key);
    }
}
