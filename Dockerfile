# -------- Stage 1: Build (JDK 21) --------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Gradle wrapper and config first (for caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build Spring Boot jar (skip tests)
RUN ./gradlew clean bootJar -x test --no-daemon

# -------- Stage 2: Runtime (JRE 21) --------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Render uses PORT env var
EXPOSE 8080

# Start application
CMD ["java", "-jar", "app.jar"]
