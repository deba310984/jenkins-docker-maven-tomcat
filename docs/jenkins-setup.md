# Jenkins Setup

How to run the `Jenkinsfile` pipeline.

## Prerequisites
- A Jenkins controller (or agent) with **Docker installed and usable by the
  Jenkins user** (the agent runs `docker build`/`docker run`).
- Plugins: *Pipeline*, *Git*, *Docker Pipeline* (optional), *JUnit*.

## 1. Configure tools
**Manage Jenkins → Tools:**
- **JDK** → add one named exactly `jdk21` (install automatically, Temurin 21).
- **Maven** → add one named exactly `maven3` (install automatically, 3.9.x).

These names match the `tools { }` block in the `Jenkinsfile`.

## 2. (Optional) Docker Hub credentials
To enable the **Docker Push** stage:
- **Manage Jenkins → Credentials** → add *Username with password*
  with ID `dockerhub`.
- Set the job/pipeline environment variable `PUSH_IMAGE=true` to turn the stage on.

Without this, the pipeline still builds, deploys locally, and smoke-tests — it
just skips pushing to a registry.

## 3. Create the pipeline job
1. **New Item → Pipeline**.
2. Under **Pipeline**, choose *Pipeline script from SCM* → *Git*.
3. Repository URL: this repo's clone URL. Branch: `main`.
4. Script path: `Jenkinsfile`.
5. Save, then **Build Now**.

## 4. What you should see
Stage view: `Checkout → Build & Test → Package WAR → Docker Build → (Docker Push)
→ Deploy → Smoke Test`, all green. Test results appear under the build's
**Test Result**; the WAR is under **Artifacts**. After deploy, the app is
reachable at `http://<agent-host>:8080/`.

## Capturing portfolio screenshots
Good screenshots to add to `architecture/images/`:
- The Jenkins **stage view** with all stages green.
- The **Test Result** page.
- The running app in a browser (`/` and `/hello?name=...`).
