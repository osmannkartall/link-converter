# Running

**Prerequisites**

- Install [Docker and Docker Compose](https://docs.docker.com/desktop/)

## Running the App with Postgres and Redis

**1. Start `Docker`.**

**2. Set the environment variables:**

```bash
export POSTGRES_USER="user"
export POSTGRES_PASSWORD="password"
export POSTGRES_DB="link-converter-db"
export SPRING_DATASOURCE_URL="jdbc:postgresql://postgres:5432/link-converter-db"
export SPRING_DATASOURCE_USERNAME="user"
export SPRING_DATASOURCE_PASSWORD="password"
export SPRING_REDIS_HOST="redis"
export SPRING_REDIS_PORT="6379"
export POSTGRES_PORT="5432"
export REDIS_PORT="6379"
export REDIS_HOST="redis"
export SPRING_PROFILES_ACTIVE="postgres-redis"
```

**3. Run the Spring Boot app along with Redis and Postgres containers:**

```bash
./start-with-postgres-redis.sh
```

----

**Stop the running containers:**

```bash
docker compose -f docker-compose-postgres-and-redis.yml --profile prod stop
```

**Remove the containers:**

```bash
docker compose -f docker-compose-postgres-and-redis.yml --profile prod down
```

**Clean up: Remove containers, volumes, and images:**

```bash
docker compose -f docker-compose-postgres-and-redis.yml --profile prod down -v --rmi all
```

## Running the System on Kubernetes

Refer to [KUBERNETES.md](k8s/KUBERNETES.md) for guidance.

## Running Only Couchbase

**1. Start `Docker`.**

**2. Add the environment variables in the run configuration:**

```bash
SPRING_COUCHBASE_BUCKET_NAME=link-conversion
SPRING_COUCHBASE_CONNECTION_STRING=couchbase://127.0.0.1
SPRING_COUCHBASE_USERNAME=Administrator
SPRING_COUCHBASE_PASSWORD=password
SPRING_PROFILES_ACTIVE=couchbase
```

**3. Set the environment variables in the terminal:**

```bash
export COUCHBASE_ADMINISTRATOR_USERNAME="Administrator"
export COUCHBASE_ADMINISTRATOR_PASSWORD="password"
```

**4. Start Couchbase:**

```bash
docker compose -f docker-compose-couchbase.yml up -d
```

**5. Initialize Couchbase cluster with servers, buckets, and indexes:**

```bash
./couchbase-init.sh
```

**6. Run the application outside the Docker network.**

## Running Only Postgres and Redis

**1. Start `Docker`.**

**2. Add the environment variables in the run configuration:**

```bash
SPRING_DATASOURCE_PASSWORD=password
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/link-converter-db
SPRING_REDIS_HOST=localhost
SPRING_DATASOURCE_USERNAME=user
SPRING_REDIS_PORT=6379
SPRING_PROFILES_ACTIVE=postgres-redis
```

**3. Set the environment variables in the terminal:**

```bash
export POSTGRES_USER="user"
export POSTGRES_PASSWORD="password"
export POSTGRES_DB="link-converter-db"
export POSTGRES_PORT="5432"
export REDIS_PORT="6379"
export REDIS_HOST="redis"
```

**4. Start Postgres and Redis:**

```bash
docker compose -f docker-compose-postgres-and-redis.yml up -d
```

**5. Run the application outside the Docker network.**

## Running Tests

**Running Unit Tests**

```bash
./gradlew test
```

This command runs the tests located in the `src/test` folder.

**Running Integration Tests**

Add the following environment variables in the test run configuration within the IDE or export them in the terminal to be used with `gradlew`:

```bash
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/link-converter-db
export SPRING_REDIS_HOST=localhost
export SPRING_DATASOURCE_USERNAME=user
export SPRING_REDIS_PORT=6379
export SPRING_COUCHBASE_CONNECTION_STRING=couchbase://127.0.0.1
export SPRING_COUCHBASE_USERNAME=Administrator
export SPRING_COUCHBASE_PASSWORD=password
export SPRING_COUCHBASE_BUCKET_NAME=link-conversion
export SPRING_PROFILES_ACTIVE=couchbase # or postgres-redis
```

Run the integration tests:

```bash
./gradlew integrationTest
```

This command runs the tests located in the `src/integration-test` folder.

**Running Load Tests**

Refer to [LOAD_TEST.md](load-test/LOAD_TEST.md) for guidance.

## Checking Data in Cache

**Couchbase**

All Records:

```bash
docker exec couchbase-db cbq -s "SELECT * FROM \`link-conversion\` WHERE META().id LIKE 'link-conversion-cache%';" -u Administrator -p password -e 127.0.0.1:8093
```

Single Record:

```bash
docker exec couchbase-db cbq -s "SELECT * FROM \`link-conversion\` WHERE META().id LIKE 'link-conversion-cache::https://li.con/fqjj14v2ra';" -u Administrator -p password -e 127.0.0.1:8093
```

**Redis**

All Records:

```bash
docker exec redis redis-cli KEYS "*"
```

Single Record:

```bash
docker exec redis redis-cli get "link-conversion-cache::https://li.con/tbci3mwf00"
```

## Accessing Couchbase Web Console

Navigate to `http://localhost:8091` and enter the following credentials:

- Username: `Administrator`  
- Password: `password`