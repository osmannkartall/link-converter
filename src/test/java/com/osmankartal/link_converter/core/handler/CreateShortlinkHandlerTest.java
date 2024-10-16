package com.osmankartal.link_converter.core.handler;

import com.osmankartal.link_converter.adapter.test.FakeLinkConversionPort;
import com.osmankartal.link_converter.adapter.test.LinkConversionTestConfig;
import com.osmankartal.link_converter.core.command.CreateShortlinkCommand;
import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.model.IdGeneratorProvider;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import com.osmankartal.link_converter.domain.service.ShortlinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateShortlinkHandlerTest {

    private static final String INVALID_URL = "invalid-url";
    private static final String INVALID_DOMAIN_FOR_URL = "https://invalid.domain.com";
    private static final String INVALID_DEEPLINK = "invalid-deeplink";
    private static final String INVALID_DOMAIN_FOR_DEEPLINK = "invalid://";

    private final IdGeneratorProvider idGeneratorProvider = new IdGeneratorProvider();
    private final ShortlinkService shortlinkService = new ShortlinkService(idGeneratorProvider);
    private final FakeLinkConversionPort fakeLinkConversionPort = new FakeLinkConversionPort();
    private final CreateShortlinkHandler createShortlinkHandler = new CreateShortlinkHandler(fakeLinkConversionPort, shortlinkService);

    @BeforeEach
    void setUp() {
        fakeLinkConversionPort.deleteAll();
    }

    @Test
    void createShortlink() {
        String deeplink = LinkConversionTestConfig.EXAMPLE_DEEPLINK + "item&id=2";
        String url = LinkConversionTestConfig.EXAMPLE_URL + "/item/12345";

        var result = createShortlinkHandler.execute(CreateShortlinkCommand.builder().url(url).deeplink(deeplink).build());

        assertNotNull(result);
        assertNotNull(result.getShortlink());
        assertEquals(deeplink, result.getDeeplink());
        assertEquals(url, result.getUrl());
    }

    @Test
    void createShortlinkWithoutDeeplink() {
        String url = LinkConversionTestConfig.EXAMPLE_URL + "/item/12345";

        var result = createShortlinkHandler.execute(CreateShortlinkCommand.builder().url(url).build());

        assertNotNull(result);
        assertNotNull(result.getShortlink());
        assertNull(result.getDeeplink());
        assertEquals(url, result.getUrl());
    }

    @Test
    void throwExceptionForInvalidUrl() {
        var createShortlinkCommand = CreateShortlinkCommand.builder().url(INVALID_URL).build();
        
        Exception e = assertThrows(LinkConversionBusinessException.class, () -> createShortlinkHandler.execute(createShortlinkCommand));

        assertEquals("url must start with " + LinkConversionConfig.BASE_URL, e.getMessage());
    }

    @Test
    void throwExceptionForUrlWithInvalidDomain() {
        var createShortlinkCommand = CreateShortlinkCommand.builder().url(INVALID_DOMAIN_FOR_URL).build();

        Exception e = assertThrows(LinkConversionBusinessException.class, () -> createShortlinkHandler.execute(createShortlinkCommand));

        assertEquals("url must start with " + LinkConversionConfig.BASE_URL, e.getMessage());
    }

    @Test
    void throwExceptionForInvalidDeeplink() {
        var createShortlinkCommand = CreateShortlinkCommand.builder()
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .deeplink(INVALID_DEEPLINK)
                .build();

        Exception e = assertThrows(LinkConversionBusinessException.class, () -> createShortlinkHandler.execute(createShortlinkCommand));

        assertEquals("deeplink must start with " + LinkConversionConfig.BASE_DEEPLINK, e.getMessage());
    }

    @Test
    void throwExceptionForDeeplinkWithInvalidDomain() {
        var createShortlinkCommand = CreateShortlinkCommand.builder()
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .deeplink(INVALID_DOMAIN_FOR_DEEPLINK)
                .build();

        Exception e = assertThrows(LinkConversionBusinessException.class, () -> createShortlinkHandler.execute(createShortlinkCommand));

        assertEquals("deeplink must start with " + LinkConversionConfig.BASE_DEEPLINK, e.getMessage());
    }

}
