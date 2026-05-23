# cms-complaints

Complaint intake and resolution for the Chemical Management System.

## Run locally

```bash
# Native Postgres (port 5432) — bootstrap via cms-platform-dev/postgres/local-native-setup.sql
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Or Docker Postgres only (port 5445):
docker compose -f ../cms-platform-dev/docker-compose.yml up -d postgres-complaints kafka
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5445/cms_complaints_db ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Service port: **8102**. API via gateway: `http://localhost:8088/api/v1/complaints/**`.

Docker all-in: `docker compose -f ../cms-platform-dev/docker-compose.yml up -d cms-complaints`

See [implementation_complaints_microservice.md](../implementation_complaints_microservice.md).
