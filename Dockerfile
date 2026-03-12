# ── Stage 1: Build ──────────────────────────────
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# 1단계: 의존성만 먼저 캐시 (build.gradle 안 바뀌면 이 레이어 재사용)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# 2단계: 소스 복사 후 빌드 (순서 중요)
COPY src ./src
RUN JAVA_OPTS="-Xmx1g" gradle clean build -x test --no-daemon

# ── Stage 2: Run ────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]