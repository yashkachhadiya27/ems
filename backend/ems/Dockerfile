FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/ems-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9090
CMD ["java", "-jar", "app.jar"]

