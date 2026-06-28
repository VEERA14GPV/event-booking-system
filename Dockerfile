## ============================================================
## STAGE 1: BUILD
## eclipse-temurin:17 matches the java.version in pom.xml.
## Maven dependency layer is cached separately from source so
## that `docker build` on a code-only change skips dependency
## resolution (the most expensive step).
## ============================================================

FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline -q

COPY src ./src
RUN mvn -B clean package -DskipTests -q

## ============================================================
## STAGE 2: RUNTIME
## Use the slim JRE image — no JDK, no build tools, smaller
## attack surface and image size (~200 MB vs ~600 MB with JDK).
## ============================================================

FROM eclipse-temurin:17-jre-jammy

LABEL org.opencontainers.image.title="event-booking-backend" \
      org.opencontainers.image.description="Spring Boot Event Booking API" \
      org.opencontainers.image.version="1.0.0"

WORKDIR /app

# curl  — used by the Docker / Kubernetes liveness HEALTHCHECK.
#          Without it every probe fails and the container is
#          permanently marked unhealthy.
# Non-root user — prevents an exploited JVM from having root
#          access to the host node.
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && groupadd --system appgroup \
 && useradd  --system --gid appgroup appuser

COPY --from=build /app/target/event-booking-system-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8082

# Docker HEALTHCHECK — used by docker-compose depends_on condition.
# Kubernetes uses the readiness/liveness probes defined in the
# Deployment manifest instead; this is a no-op there but harmless.
HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=3 \
  CMD curl -f http://localhost:8082/actuator/health || exit 1

# JVM tuning for containerised environments:
#   UseContainerSupport  — respect cgroup memory limits.
#   MaxRAMPercentage     — use at most 75 % of the container's RAM.
#   ExitOnOutOfMemoryError — crash fast so K8s restarts the pod rather
#                            than running in a degraded OOM state.
#   spring.profiles.active — defaults to empty (dev); override via
#                            SPRING_PROFILES_ACTIVE env var in K8s.
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-Dspring.devtools.restart.enabled=false", \
  "-jar", "app.jar"]
