# Troubleshooting

| Symptom | Likely cause | Fix |
|---------|--------------|-----|
| `404` for the app at `/` | WAR not deployed as `ROOT.war`, or wrong context | Confirm the Dockerfile copies to `webapps/ROOT.war` and the default apps were removed |
| `ClassNotFoundException: javax.servlet...` | Mixing `javax` code with Tomcat 10+ (which uses `jakarta`) | Use `jakarta.servlet` imports with Tomcat 10/11, or Tomcat 9 with `javax` |
| `mvn` build fails: cannot reach `repo.maven.apache.org` | No network/proxy from the build environment | Ensure the build host can reach Maven Central (or configure a mirror in `settings.xml`) |
| Jenkins: `docker: not found` | Docker not installed on the agent, or Jenkins user lacks access | Install Docker on the agent; add the `jenkins` user to the `docker` group |
| Jenkins: tool `jdk21`/`maven3` not found | Tool names don't match | Create tools with those exact names (Manage Jenkins → Tools) |
| Port `8080` already in use on deploy | A previous container is still running | The pipeline runs `docker rm -f` first; locally, `docker rm -f java-tomcat-app` |
| Smoke test times out | Tomcat still starting, or app failed to deploy | `docker logs <container>`; increase the smoke-test retries/sleep |
| Image is huge | Single-stage build shipping Maven + JDK | Use the multi-stage `Dockerfile` (build stage discarded) |

## Useful commands
```bash
docker logs java-tomcat-app          # application / Tomcat logs
docker exec -it java-tomcat-app bash # shell inside the container
docker images                         # confirm image size
mvn -B clean verify                   # build + test locally
curl -i http://localhost:8080/        # check HTTP status + headers
```
