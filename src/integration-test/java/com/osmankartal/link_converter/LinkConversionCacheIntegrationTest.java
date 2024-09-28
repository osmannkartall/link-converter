package com.osmankartal.link_converter;

import com.osmankartal.link_converter.adapter.test.LinkConversionTestConfig;
import com.osmankartal.link_converter.core.port.LinkConversionPort;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class LinkConversionCacheIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    LinkConversionPort linkConversionPort;

    @BeforeEach
    void setUp() {
        Objects.requireNonNull(cacheManager.getCache("link-conversion-cache")).clear();
    }

    @Test
    void saveAndFetch() {
        var linkConversion = LinkConversion.builder()
                .id("1")
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .deeplink(LinkConversionTestConfig.EXAMPLE_DEEPLINK)
                .shortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        linkConversionPort.save(linkConversion);

        var cache = Objects.requireNonNull(cacheManager.getCache("link-conversion-cache"));
        var recordInCache = (LinkConversion) Objects.requireNonNull(cache.get(LinkConversionTestConfig.EXAMPLE_SHORTLINK)).get();
        assertNotNull(recordInCache);
        assertEquals(linkConversion.getUrl(), recordInCache.getUrl());
        assertEquals(linkConversion.getDeeplink(), recordInCache.getDeeplink());
        assertEquals(linkConversion.getShortlink(), recordInCache.getShortlink());
        assertEquals(linkConversion.getCreatedAt(), recordInCache.getCreatedAt());
        assertEquals(linkConversion.getUpdatedAt(), recordInCache.getUpdatedAt());
    }

}
