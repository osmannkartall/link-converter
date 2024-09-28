# Link Converter

Link Converter is a [URL shortening](https://en.wikipedia.org/wiki/URL_shortening) project with support for [mobile deeplinking](https://en.wikipedia.org/wiki/Mobile_deep_linking).

Link Converter is a **Spring Boot** application built with **Java 22**, integrating **Postgres**, **Redis**, and **Couchbase**, and following the principles of **Hexagonal Architecture**.

## Running

This section explains how to run the `Spring Boot` app with `Couchbase` integration. It creates one Spring Boot container and one Couchbase container. Refer to [RUNNING.md](RUNNING.md) for other running options.

`Couchbase` has caching support and [auto-sharding](https://docs.couchbase.com/server/current/learn/buckets-memory-and-storage/vbuckets.html) feature to distribute data across multiple servers. Therefore, no additional configuration is required for these features.

**Prerequisites**

- Install [Docker and Docker Compose](https://docs.docker.com/desktop/)

**1. Start `Docker`**

**2. Set the environment variables:**

```bash
export SPRING_COUCHBASE_CONNECTION_STRING="couchbase://couchbase-db"
export SPRING_COUCHBASE_USERNAME="Administrator"
export SPRING_COUCHBASE_PASSWORD="password"
export SPRING_COUCHBASE_BUCKET_NAME="link-conversion"
export COUCHBASE_ADMINISTRATOR_USERNAME="Administrator"
export COUCHBASE_ADMINISTRATOR_PASSWORD="password"
export SPRING_PROFILES_ACTIVE="couchbase"
```

**3. Run the Spring Boot app and Couchbase containers:**

```bash
./start-with-couchbase.sh
```

----

**Stop the running containers:**

```bash
docker compose -f docker-compose-couchbase.yml --profile prod stop
```

**Remove the containers:**

```bash
docker compose -f docker-compose-couchbase.yml --profile prod down
```

**Clean Up: Remove containers, volumes, and images:**

```bash
docker compose -f docker-compose-couchbase.yml --profile prod down -v --rmi all
```

**Note:** After the `docker compose down` command, it may be necessary to delete the existing volume to create a cluster from scratch.

## Usage

**1. Create a shortlink from "url". "deeplink" is optional.**

```bash
curl -X POST http://localhost:8080/link_conversions \
    -H "Content-Type: application/json" \
    -d '{
        "deeplink": "app://item&id=12345",
        "url": "https://any.domain.com/item/12345"
    }'
```

Response:

Note that the value of the shortlink will be different from "y3e3m1d75v".

```json
{
    "shortlink": "https://li.con/y3e3m1d75v"
}
```

**2. Get the long url and deeplink for this shortlink**

```bash
curl -X GET "http://localhost:8080/link_conversions?hash=y3e3m1d75v"
```

Response:

```json
{
   "deeplink": "app://item&id=12345",
   "url": "https://any.domain.com/item/12345"
}
```

**Note:** Example requests can be found at `http://localhost:8080/swagger-ui/index.html#/`

## System Design

**Data Model**

There are only two models: `link-conversion` and `link-conversion-failure`. `link-conversion-failure` is used only to store information about failed requests.

**link-conversion**

```json
{
    "id": 1,
    "url": "https://any.domain.com",
    "deeplink": "app://home",
    "shortlink": "https://li.con/123456789a",
    "createdAt": "2024-09-18T14:30:45",
    "updatedAt": "2024-09-18T14:40:10"
}
```

**Indexing**

There is index on `shortlink` field for fast querying.

**Caching**

`link-conversion` records are stored in the cache if their `shortlink` fields are not empty for fast retrieval.

**Scaling**

Refer to [KUBERNETES.md](k8s/KUBERNETES.md) for a minimal version of the scalable architecture.

Currently:

- Load tests performed on the host machine cannot be performed on the Kubernetes cluster.
- The Kubernetes cluster maintains 2 to 4 copies of the Spring Boot app with auto-scaling.
- Three instances of Couchbase cluster servers are created in the Kubernetes cluster. Link conversion records are automatically distributed among these three instances. There is no auto-scaling for Couchbase servers.

## Development

Refer to [DEVELOPMENT.md](DEVELOPMENT.md) for guidance.