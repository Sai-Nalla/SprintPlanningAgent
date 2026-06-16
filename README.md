# Sprint Planning Agent - Module 8 Standalone

This project contains files for the Sprint Planning Agent, with PostgreSQL + pgvector and Docker support preserved for RAG/vector-search compatibility.

## Included Module 8 files

- `SprintPlanningController.java`
- `SprintPlanningService.java`
- `SprintPlanningTools.java`
- `SprintDataLoader.java`
- `UserStory.java`
- `VelocityResult.java`
- `ComplexityAnalysis.java`
- `DependencyCheck.java`
- `SprintRecommendation.java`
- `SprintPlanRequest.java`
- `StoryRepository.java`
- `sprint-planning-agent.html`

## Requirements

- Java 21
- Docker Desktop
- OpenAI API key

## Run with Docker Compose

Create an `.env` file in the project root:

```bash
OPENAI_API_KEY=your_openai_api_key_here
```

Start PostgreSQL/pgvector and the Spring Boot app:

```bash
docker compose up --build
```

Open the UI:

```text
http://localhost:8081/sprint-planning-agent.html
```

## Run locally with PostgreSQL/pgvector in Docker

Start only the pgvector database:

```bash
docker compose up -d postgres-pgvector
```

Run the Java app locally:

```bash
export OPENAI_API_KEY=your_openai_api_key_here
./mvnw spring-boot:run
```

Open:

```text
http://localhost:8081/sprint-planning-agent.html
```

## Database settings

Default local database configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_ecommerce
spring.datasource.username=admin
spring.datasource.password=admin123
```

The pgvector schema is initialized automatically through:

```properties
spring.ai.vectorstore.pgvector.initialize-schema=true
spring.ai.vectorstore.pgvector.index-type=HNSW
spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE
spring.ai.vectorstore.pgvector.dimensions=1536
```

## API endpoints

```text
GET  /api/sprint/backlog
POST /api/sprint/plan
POST /api/sprint/stories/reload
```

Example request:

```bash
curl -X POST http://localhost:8081/api/sprint/plan \
  -H "Content-Type: application/json" \
  -d '{"userQuery":"Plan the next sprint based on team velocity"}'
```
