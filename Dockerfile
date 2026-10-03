# syntax=docker/dockerfile:1

# ---- Stage 1: build the WAR with Maven ----
# A multi-stage build keeps the final image small: the JDK, Maven, and the local
# dependency cache never ship to production — only the runtime and the WAR do.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the POM first and pre-fetch dependencies so this layer is cached and only
# re-runs when pom.xml changes (not on every source edit).
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now copy sources and build. Tests run here, so a failing test fails the build.
COPY src ./src
RUN mvn -B -q clean package

# ---- Stage 2: runtime (Tomcat only) ----
FROM tomcat:10.1-jdk21-temurin
# Remove the default Tomcat apps and deploy our WAR as the ROOT context,
# so it is served at "/" instead of "/java-tomcat-app".
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/java-tomcat-app.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Container-level health check the orchestrator (or Docker) can act on.
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=3 \
  CMD curl -fsS http://localhost:8080/ || exit 1

CMD ["catalina.sh", "run"]
