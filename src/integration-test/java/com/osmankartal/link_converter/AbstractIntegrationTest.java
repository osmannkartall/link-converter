package com.osmankartal.link_converter;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Paths;


abstract class AbstractIntegrationTest {

    private static final String LINK_CONVERSION_BUCKET_NAME = "link-conversion";
    private static final String LINK_CONVERSION_FAILURE_BUCKET_NAME = "link-conversion-failure";

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.4-alpine")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(Paths.get("postgres-init.sql")),
                    "/docker-entrypoint-initdb.d/postgres-init.sql"
            );
    static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.4-alpine"))
            .withExposedPorts(6379);
    static CouchbaseContainer couchbaseContainer = new CouchbaseContainer(
            DockerImageName.parse("couchbase:enterprise-7.6.3").asCompatibleSubstituteFor("couchbase/server:enterprise-7.6.3"))
            .withExposedPorts(8091)
            .withCredentials("Administrator", "password")
            .withBucket(new BucketDefinition(LINK_CONVERSION_BUCKET_NAME))
            .withBucket(new BucketDefinition(LINK_CONVERSION_FAILURE_BUCKET_NAME));

    static {
        postgreSQLContainer.start();
        redisContainer.start();
        couchbaseContainer.start();
    }

    @DynamicPropertySource
    public static void setupContainers(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);

        registry.add("spring.couchbase.connection-string", couchbaseContainer::getConnectionString);
        registry.add("spring.couchbase.username", couchbaseContainer::getUsername);
        registry.add("spring.couchbase.password", couchbaseContainer::getPassword);
        registry.add("spring.couchbase.bucket.name", () -> LINK_CONVERSION_BUCKET_NAME);
    }

}
