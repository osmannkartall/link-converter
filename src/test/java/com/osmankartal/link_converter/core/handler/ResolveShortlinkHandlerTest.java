package com.osmankartal.link_converter.core.handler;

import com.osmankartal.link_converter.adapter.test.FakeLinkConversionPort;
import com.osmankartal.link_converter.adapter.test.LinkConversionTestConfig;
import com.osmankartal.link_converter.core.command.ResolveShortlinkCommand;
import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.exception.LinkConversionNotFoundException;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import com.osmankartal.link_converter.domain.service.ShortlinkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResolveShortlinkHandlerTest {

    private final ShortlinkService shortlinkService = new ShortlinkService();
    private final FakeLinkConversionPort fakeLinkConversionPort = new FakeLinkConversionPort();
    private final ResolveShortlinkHandler resolveShortlinkHandler = new ResolveShortlinkHandler(fakeLinkConversionPort, shortlinkService);

    @BeforeEach
    void setUp() {
        fakeLinkConversionPort.deleteAll();
    }

    @Test
    void resolveShortlink() {
        String deeplink = LinkConversionTestConfig.EXAMPLE_DEEPLINK + "item&id=2";
        String url = LinkConversionTestConfig.EXAMPLE_URL + "/item/12345";
        String shortlink = LinkConversionTestConfig.EXAMPLE_SHORTLINK;
        var linkConversion = LinkConversion.builder()
                .id("1")
                .deeplink(deeplink)
                .url(url)
                .shortlink(shortlink)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        fakeLinkConversionPort.save(linkConversion);

        assertEquals(linkConversion, resolveShortlinkHandler.execute(ResolveShortlinkCommand.builder().shortlink(shortlink).build()));
    }

    @Test
    void throwExceptionWhenShortlinkIsNotFound() {
        String shortlink = LinkConversionConfig.SHORTLINK_DOMAIN + "/shortlink-not-found";

        var resolveShortlinkCommand = ResolveShortlinkCommand.builder().shortlink(shortlink).build();
        Exception e = assertThrows(LinkConversionNotFoundException.class, () -> resolveShortlinkHandler.execute(resolveShortlinkCommand));

        assertEquals("Cannot find the record for the given shortlink", e.getMessage());
    }

    @Test
    void throwExceptionForInvalidShortlink() {
        var resolveShortlinkCommand = ResolveShortlinkCommand.builder().shortlink("invalid-shortlink").build();

        Exception e = assertThrows(LinkConversionBusinessException.class, () -> resolveShortlinkHandler.execute(resolveShortlinkCommand));

        assertEquals("shortlink must start with " + LinkConversionConfig.SHORTLINK_DOMAIN, e.getMessage());
    }
}
