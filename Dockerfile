# 빌드 과정 없이, 밖에서 만든(workflow) jar 파일만 가져다 쓰기
FROM eclipse-temurin:17-jdk
EXPOSE 8080
# Github Actions에서 빌드된 cicdtest.jar를 복사
COPY cicdtest.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]