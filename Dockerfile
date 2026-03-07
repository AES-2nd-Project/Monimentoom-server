# 1. 빌드 스테이지
FROM gradle:7.6-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# 테스트는 제외하고 빌드하여 속도 향상
RUN gradle build --no-daemon -x test

# 2. 실행 스테이지
FROM eclipse-temurin:17-jdk
EXPOSE 8080
# 빌드 스테이지에서 생성된 jar 파일만 복사
COPY --from=build /home/gradle/src/build/libs/*.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]