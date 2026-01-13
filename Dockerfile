# -------- Stage 1: Build the jar using Gradle --------
FROM gradle:8.3-jdk17 AS build

# Set working directory
WORKDIR /app

# Copy project files
COPY --chown=gradle:gradle . .

# Build the jar (skip tests for faster build, remove --no-daemon if you prefer)
RUN gradle clean build -x test --no-daemon

# -------- Stage 2: Run the jar --------
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the port (Render uses PORT env variable)
EXPOSE 8080

# Run the jar
CMD ["java", "-jar", "app.jar"]
