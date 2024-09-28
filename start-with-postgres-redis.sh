#!/bin/bash

echo "Building the application..."
# ./gradlew clean build
# couchbase cluster creation is time consuming in integration tests so integration tests are
# excluded when building the application.
# They can be run separately with the `./gradlew integrationTest` command
./gradlew clean build -x integrationTest
echo "Build the app successfully."

echo "Creating docker containers: app, postgres, redis..."
docker compose -f docker-compose-postgres-and-redis.yml --profile prod up -d