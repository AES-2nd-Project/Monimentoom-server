# ── Stage 1: Build ──────────────────────────────
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# 의존성 레이어 캐시 분리
# build.gradle이 바뀌지 않으면 이 레이어는 캐시 재사용
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 소스 복사 & 빌드
COPY src ./src
RUN gradle clean build -x test --no-daemon

# ── Stage 2: Run ────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]