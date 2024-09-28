package com.osmankartal.link_converter.adapter.cache;

import com.osmankartal.link_converter.adapter.persistence.document.LinkConversionDocument;
import com.osmankartal.link_converter.adapter.persistence.document.LinkConversionFailureDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.CouchbaseClientFactory;
import org.springframework.data.couchbase.SimpleCouchbaseClientFactory;
import org.springframework.data.couchbase.cache.CouchbaseCacheConfiguration;
import org.springframework.data.couchbase.cache.CouchbaseCacheManager;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.config.EnableCouchbaseRepositories;
import org.springframework.data.couchbase.repository.config.RepositoryOperationsMapping;

@Configuration
@Profile("couchbase")
@EnableCouchbaseRepositories(basePackages = {"com/osmankartal/link_converter/adapter/persistence/repository"})
@EnableCaching
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

    @Value("${spring.couchbase.connection-string}")
    private String connectionString;

    @Value("${spring.couchbase.username}")
    private String username;

    @Value("${spring.couchbase.password}")
    private String password;

    @Value("${spring.couchbase.bucket-name}")
    private String bucketName;

    @Override
    public String getConnectionString() {
        return connectionString;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getBucketName() {
        return bucketName;
    }

    @Bean
    public CouchbaseCacheManager cacheManager(CouchbaseTemplate couchbaseTemplate) {
        CouchbaseCacheManager.CouchbaseCacheManagerBuilder builder = CouchbaseCacheManager.CouchbaseCacheManagerBuilder
                .fromConnectionFactory(couchbaseTemplate.getCouchbaseClientFactory());
        builder.withCacheConfiguration("link-conversion-cache", CouchbaseCacheConfiguration.defaultCacheConfig());
        return builder.build();
    }

    @Override
    public void configureRepositoryOperationsMapping(RepositoryOperationsMapping baseMapping) {
        baseMapping
                .mapEntity(LinkConversionDocument.class, linkConversionTemplate())
                .mapEntity(LinkConversionFailureDocument.class, linkConversionFailureTemplate());
    }

    @Bean("linkConversionClientFactory")
    @Primary
    public CouchbaseClientFactory linkConversionClientFactory() {
        return new SimpleCouchbaseClientFactory(getConnectionString(), authenticator(), "link-conversion");
    }

    @Bean("linkConversionTemplate")
    public CouchbaseTemplate linkConversionTemplate() {
        return new CouchbaseTemplate(linkConversionClientFactory(), new MappingCouchbaseConverter());
    }

    @Bean("linkConversionFailureClientFactory")
    public CouchbaseClientFactory linkConversionFailureClientFactory() {
        return new SimpleCouchbaseClientFactory(getConnectionString(), authenticator(), "link-conversion-failure");
    }

    @Bean("linkConversionFailureTemplate")
    public CouchbaseTemplate linkConversionFailureTemplate() {
        return new CouchbaseTemplate(linkConversionFailureClientFactory(), new MappingCouchbaseConverter());
    }
}