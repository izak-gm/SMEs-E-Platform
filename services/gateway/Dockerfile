# Building layer
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy pom.xml and download dependencies
COPY pom.xml
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
RUN ls -la /app/target/
# caching
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install wget for HealthCheck
RUN apk add --no-cache wget

# create a non-root user  for spring boot
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/*.jar app.jar
# Set ownership
RUN chown spring:spring app.jar
USER spring:spring

# Copy jar and explode it for execution
COPY --from=build /app/target/*.jar app.jar
# expose port here or in K3s
EXPOSE 8222
# HealthCheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=10 --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8222/actutor/health || exit 1
ENTRYPOINT ["java", "-jar", "-Xmx256m", "app.jar"]