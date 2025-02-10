# Use a minimal JDK 17 base image for building

FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy and build the application

COPY . .

RUN ./mvnw clean package -DskipTests

# Use a lightweight JRE 17 image for runtime

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from the builder stage

COPY --from=builder /app/target/*.jar app.jar

# Expose the application port

EXPOSE 8080

# Run the Spring Boot application

CMD ["java", "-jar", "app.jar"]

