# Stage 1 — build (we already built, so we just copy the JAR)
FROM eclipse-temurin:17-jre-alpine

# Create non-root user for security
RUN addgroup -S payments && adduser -S payments -G payments

WORKDIR /app

# Copy the built JAR
COPY target/payment-service-0.0.1-SNAPSHOT.jar app.jar

# Switch to non-root user
USER payments

# Expose the port
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]