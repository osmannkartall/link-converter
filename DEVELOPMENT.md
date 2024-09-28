# DEVELOPMENT

This document provides an overview of how the link conversion system works, with instructions for testing, debugging, and adding new features.

## Getting Started with Development

Refer to [RUNNING.md](RUNNING.md) for guidance.

## Project Structure

The project is based on Hexagonal Architecture.

The `LinkConversionPort` class serves as the entry point to the domain. Command handlers execute commands coming from outside through this port.

There are two different implementations of this port:
- The first one uses **Postgres** and **Redis** as the database and cache. Its corresponding Spring Profile is called `postgres-redis`.
- The second one uses **Couchbase** for both the database and cache. Its corresponding Spring Profile is called `couchbase`.
- The implementation is determined by setting the `spring.profiles.active` property in `application.yml`.
- References to the `LinkConversionPort` interface are automatically loaded with the selected implementation.

There is also a fake implementation of this port for unit testing the domain layer.

Handlers only use the port, receive the `LinkConversion` entities, send them to the domain layer. They do not contain business logic, leaving it to the domain layer.

## Cache Implementation

Caching is implemented using [Declarative Annotation-based Caching](https://docs.spring.io/spring-framework/reference/integration/cache/annotations.html).

Methods annotated with `@Cacheable` can be debugged to ensure that they are not called when the corresponding record is already in the cache.

## Writing Unit Tests

Use test doubles instead of mocks. Refer to `FakeLinkConversionPort.java` for an example.

It is essential to write unit tests, especially for command handlers.

Specific units should be tested separately with a variety of input sets.

## Writing Integration Tests

Integration tests that require infrastructure are based on [Testcontainers](https://testcontainers.com/).

Extend the `AbstractIntegrationTest.java` class when creating new infrastructure integration tests. The `AbstractIntegrationTest.java` class creates a single instance each of `Postgres`, `Redis`, and `Couchbase`. The same instances are used across all integration tests.

It is necessary to flush all data in these infrastructures before running each test scenario.