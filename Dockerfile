FROM eclipse-temurin:17-jdk-alpine
LABEL authors="victor"
EXPOSE 8080
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]