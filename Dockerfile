# ============================================================
# STAGE 1: BUILD
# ============================================================

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

# ============================================================
# STAGE 2: RUNTIME
# ============================================================

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system appgroup \
 && useradd --system --gid appgroup appuser

COPY --from=build /app/target/event-booking-system-0.0.1-SNAPSHOT.jar app.jar

RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
CMD curl -f http://localhost:${PORT:-8082}/actuator/health || exit 1

ENTRYPOINT ["java",
"-XX:+UseContainerSupport",
"-XX:MaxRAMPercentage=75.0",
"-XX:+ExitOnOutOfMemoryError",
"-Djava.security.egd=file:/dev/./urandom",
"-Dspring.devtools.restart.enabled=false",
"-jar",
"app.jar"]
