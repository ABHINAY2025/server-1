# =========================
# Stage 1: Build the JAR
# =========================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app
COPY . .

# ✅ Fix permission issue
RUN chmod +x mvnw

# Build the JAR (skip tests for faster builds)
RUN ./mvnw clean package -DskipTests

# =========================
# Stage 2: Run the JAR
# =========================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the built jar from the builder
COPY --from=builder /app/target/*.jar app.jar

# Expose your Spring Boot port
EXPOSE 5000

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
