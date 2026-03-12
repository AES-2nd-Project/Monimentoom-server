package com.example.monimentoom.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import com.example.monimentoom.exception.CustomException;
import com.example.monimentoom.exception.ErrorCode;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    // 확장자로 ContentType 자동 판단 (지원하지 않는 형식은 예외 처리)
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
     * 클라이언트가 S3에 직접 업로드할 수 있는 Presigned URL 발급
     *
     * @param dirName:          S3에서 저장폴더명 (예: "goods", "user")
     * @param originalfileName: 원본 파일명 (UUID 붙여서 중복 방지)
     */
    public PresignedUrlResult generatePresignedUrl(String dirName, String originalfileName) {
        String contentType = resolveContentType(originalfileName);
        // 파일명 중복 방지
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + originalfileName;

        // S3Presigner를 try with resource로 자동 close
        try (S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .region(Region.of(region))
                .build()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(contentType) // 서명에 포함 -> PUT 요청 시 Content-Type 헤더 필수
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5)) // URL 유효시간 5분
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            String presignedUrl = presignedRequest.url().toString();
            String imageUrl = extractImageUrl(presignedUrl);
            return new PresignedUrlResult(presignedUrl, imageUrl, contentType);
        }
    }

    // presignedUrl: 클라이언트가 S3에 PUT 요청할 URL
    // imageUrl: DB에 저장할 순수 S3 URL (쿼리스트링 없음)
    public record PresignedUrlResult(String presignedUrl, String imageUrl, String contentType) {}

    // Presigned URL에서 쿼리스트링 제거 -> DB 저장용 imageUrl 추출
    public String extractImageUrl(String presignedUrl) {
        return presignedUrl.split("\\?")[0];
    }

    /**
     * S3에서 파일 삭제
     * @param imageUrl : DB에 저장된 순수 S3 url
     * URL에서 버킷 이후 경로(key)만 추출해서 삭제 요청
     */
    public void deleteFile(String imageUrl) {
        // https://버킷명.s3.리전.amazonaws.com/goods/uuid_test.jpg 에서 key만 추출
        String key = imageUrl.substring(imageUrl.indexOf(".amazonaws.com/") + ".amazonaws.com/".length());
        s3Client.deleteObject(builder -> builder.bucket(bucket).key(key).build());
        log.info("S3 파일 삭제 완료: {}", key);
    }

}
