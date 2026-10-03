# Implementation Notes

## What this project is
A complete CI/CD slice for a Java web application: Maven builds and tests the
code, packages it as a WAR, a multi-stage Docker image bakes that WAR into a
Tomcat runtime, and a Jenkins pipeline drives the whole flow from commit to a
running, health-checked container.

## Design decisions & trade-offs

### Multi-stage Docker build
The `Dockerfile` has two stages:
1. **build** (`maven:3.9-eclipse-temurin-21`) compiles and tests the code and
   produces the WAR.
2. **runtime** (`tomcat:10.1-jdk21-temurin`) contains only Tomcat + the WAR.

The JDK, Maven, and the dependency cache never ship to production, so the final
image is far smaller and has a smaller attack surface. Copying `pom.xml` and
running `dependency:go-offline` before copying sources means the dependency
layer is cached and only re-downloads when dependencies actually change.

### WAR deployed as ROOT
The WAR is copied to `webapps/ROOT.war` so the app is served at `/` rather than
`/java-tomcat-app`, which is what you want for a single-app container.

### Jakarta namespace + Tomcat 10
The servlet uses the `jakarta.servlet` namespace (Servlet 6), which is what
Tomcat 10.x expects. (Tomcat 9 and earlier use the older `javax.servlet`
namespace — mixing them is the most common "404 / ClassNotFound" cause.)

### Business logic separated from the servlet
`GreetingService` holds the logic and is unit-tested without a servlet
container; `HelloServlet` is a thin HTTP adapter. This keeps tests fast and the
code testable.

### Pipeline ordering
`Build & Test → Package → Docker Build → (Push) → Deploy → Smoke Test`.
Tests run before any image is built, so a failing test stops the pipeline early.
The deploy is followed by an automated smoke test (`curl` the health endpoint)
so a broken deploy fails the build instead of silently shipping.

## Verification performed
- `mvn clean verify` — WAR built (`target/java-tomcat-app.war`), 4 unit tests pass.
- The built WAR was run in the `tomcat:10.1-jdk21-temurin` image; the container
  served both `index.jsp` and the `/hello` servlet successfully.
- The multi-stage `Dockerfile` and the Maven build are exercised in CI
  (`.github/workflows/ci.yml`).

## Running it yourself
- **Local (Docker only):** `docker compose up --build`, then open <http://localhost:8080>.
- **Full pipeline:** import the repo into Jenkins as a Pipeline job using the
  `Jenkinsfile`; see [jenkins-setup.md](jenkins-setup.md).
