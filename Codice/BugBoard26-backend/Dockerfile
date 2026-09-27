FROM eclipse-temurin:23-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar
COPY Keys/ ./Keys/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]