Banking backend (Spring Boot)

Build:
mvn -f backend/pom.xml clean package

Run:
mvn -f backend/pom.xml spring-boot:run

Notes:
- Configure database connection in `backend/src/main/resources/application.properties` or provide env vars.
- Java 21+ is required.

CI note: GitHub Actions is configured to run the backend test suite (see `.github/workflows/maven.yml`). If you don't have Maven locally, push your branch/PR and CI will run tests and upload surefire reports for debugging.
