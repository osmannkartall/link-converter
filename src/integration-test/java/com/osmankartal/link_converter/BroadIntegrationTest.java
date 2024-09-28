package com.osmankartal.link_converter;

import com.osmankartal.link_converter.adapter.rest.request.CreateShortlinkRequest;
import com.osmankartal.link_converter.adapter.rest.response.CreateShortlinkResponse;
import com.osmankartal.link_converter.adapter.rest.response.ResolveShortlinkResponse;
import com.osmankartal.link_converter.core.port.LinkConversionPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BroadIntegrationTest extends AbstractIntegrationTest {

    private static final String URL = "https://any.domain.com/item/12345";

    private static final String DEEPLINK = "app://item&id=12345";

    @Autowired
    LinkConversionPort linkConversionPort;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        linkConversionPort.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("link-conversion-cache")).clear();
    }

    @Test
    void createShortlinkAndFetch() {
        var createShortlinkResponse = createShortlink();
        var resolveShortlinkResponse = resolveShortlink(Objects.requireNonNull(createShortlinkResponse.getBody()).getShortlink());
        var expectedServeShortlinkResponse = ResolveShortlinkResponse.builder()
                .url(URL)
                .deeplink(DEEPLINK)
                .build();

        assertEquals(HttpStatus.OK, resolveShortlinkResponse.getStatusCode());
        assertEquals(expectedServeShortlinkResponse, resolveShortlinkResponse.getBody());
    }

    @Test
    void createShortlinkAndValidateInDBAndCache() {
        var createShortlinkResponse = createShortlink();
        var linkConversionInDB = linkConversionPort.resolveShortlink(Objects.requireNonNull(createShortlinkResponse.getBody()).getShortlink());
        var cache = Objects.requireNonNull(cacheManager.getCache("link-conversion-cache"));
        var linkConversionInCache = Objects.requireNonNull(cache.get(Objects.requireNonNull(createShortlinkResponse.getBody()).getShortlink()));

        assertNotNull(createShortlinkResponse);
        assertEquals(HttpStatus.OK, createShortlinkResponse.getStatusCode());
        assertTrue(linkConversionInDB.isPresent());
        assertNotNull(createShortlinkResponse.getBody());
        assertEquals(createShortlinkResponse.getBody().getShortlink(), linkConversionInDB.get().getShortlink());
        assertEquals(linkConversionInDB.get(), linkConversionInCache.get());
    }

    private ResponseEntity<CreateShortlinkResponse> createShortlink() {
        var request = CreateShortlinkRequest.builder().url(URL).deeplink(DEEPLINK).build();
        var entity = new HttpEntity<>(request, new HttpHeaders());
        return restTemplate.postForEntity("/link_conversions", entity, CreateShortlinkResponse.class);
    }

    private ResponseEntity<ResolveShortlinkResponse> resolveShortlink(String shortlink) {
        String[] shortlinkParts = shortlink.split("/");
        String queryParameter = "hash=" + shortlinkParts[shortlinkParts.length-1];

        return restTemplate.getForEntity("/link_conversions?" + queryParameter, ResolveShortlinkResponse.class);
    }

}
