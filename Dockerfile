# ── Stage 1: Build ──────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

RUN apk add --no-cache bash

COPY gradlew ./
RUN chmod +x gradlew
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# || true 제거 → 실패 시 즉시 중단
RUN bash ./gradlew dependencies --no-daemon

COPY src ./src
RUN bash ./gradlew clean build -x test --no-daemon

# ── Stage 2: Run ────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]