# ─────────────────────────────────────────────────────────────────────────────
# Stage 1 — Build
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS build

# Install Maven
RUN apt-get update -qq \
    && apt-get install -y -qq maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Cache dependencies separately from source — only re-downloaded if pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Build the fat jar (tests run separately in CI)
COPY src ./src
RUN mvn package -DskipTests -q

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2 — Runtime (minimal JRE, no build tools)
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy AS runtime

WORKDIR /app

# Run as non-root for security
RUN addgroup --system appgroup \
    && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
