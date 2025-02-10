# Build stage
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/FreshSkinWeb-0.0.1-SNAPSHOT.jar FreshSkinWeb.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "FreshSkinWeb.jar"]
