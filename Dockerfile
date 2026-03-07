FROM eclipse-temurin:17-jdk
EXPOSE 8080

COPY monimentoom.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]