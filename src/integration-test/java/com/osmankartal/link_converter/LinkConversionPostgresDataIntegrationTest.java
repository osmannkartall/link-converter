package com.osmankartal.link_converter;


import com.osmankartal.link_converter.adapter.persistence.PostgresAndRedisAdapter;
import com.osmankartal.link_converter.adapter.persistence.entity.LinkConversionJPAEntity;
import com.osmankartal.link_converter.adapter.persistence.repository.LinkConversionJPARepository;
import com.osmankartal.link_converter.adapter.test.LinkConversionTestConfig;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringJUnitConfig
@ActiveProfiles("postgres-redis")
class LinkConversionPostgresDataIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    LinkConversionJPARepository linkConversionJPARepository;

    @Autowired
    PostgresAndRedisAdapter postgresAndRedisAdapter;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void tearDown() {
        postgresAndRedisAdapter.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("link-conversion-cache")).clear();
    }

    @Test
    @EnabledIf(expression = "#{environment.matchesProfiles('postgres-redis')}", loadContext = true)
    void saveAndFetchLinkConversion() {
        LocalDateTime now = LocalDateTime.now();
        LinkConversion linkConversion = LinkConversion.builder()
                .id("1")
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .deeplink(LinkConversionTestConfig.EXAMPLE_DEEPLINK)
                .shortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK)
                .createdAt(now)
                .updatedAt(now)
                .build();

        postgresAndRedisAdapter.save(linkConversion);

        Optional<LinkConversionJPAEntity> linkConversionJPAEntity = linkConversionJPARepository.findByShortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK);
        assertTrue(linkConversionJPAEntity.isPresent());
        assertEquals(linkConversion.getUrl(), linkConversionJPAEntity.get().toModel().getUrl());
        assertEquals(linkConversion.getDeeplink(), linkConversionJPAEntity.get().toModel().getDeeplink());
        assertEquals(linkConversion.getShortlink(), linkConversionJPAEntity.get().toModel().getShortlink());
        assertEquals(linkConversion.getCreatedAt(), linkConversionJPAEntity.get().toModel().getCreatedAt());
        assertEquals(linkConversion.getUpdatedAt(), linkConversionJPAEntity.get().toModel().getUpdatedAt());
    }
}
