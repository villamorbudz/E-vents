FROM maven:3.9.9-openjdk-17 AS build
WORKDIR /app
COPY e-vents_backend/ ./
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
