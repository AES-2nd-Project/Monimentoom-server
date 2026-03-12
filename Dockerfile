# ── Stage 1: Build ──────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# gradlew 사용을 위해 전체 프로젝트 구조 필요
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# 의존성 캐시
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew clean build -x test --no-daemon

# ── Stage 2: Run ────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]