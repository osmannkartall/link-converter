package com.osmankartal.link_converter.adapter.test;

import com.osmankartal.link_converter.core.command.ResolveShortlinkCommand;
import com.osmankartal.link_converter.core.handler.Handler;
import com.osmankartal.link_converter.domain.exception.LinkConversionNotFoundException;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import org.springframework.stereotype.Service;

@Service
public class FakeResolveShortlinkHandler implements Handler<ResolveShortlinkCommand, LinkConversion> {

    private static final String SHORTLINK_NOT_FOUND = LinkConversionConfig.SHORTLINK_DOMAIN + "/shortlink-not-found";

    @Override
    public LinkConversion execute(ResolveShortlinkCommand command) {
        if (SHORTLINK_NOT_FOUND.equals(command.getShortlink())) {
            throw new LinkConversionNotFoundException("Cannot find the record for the given shortlink", command.getShortlink());
        }

        return LinkConversion.builder()
                .id("1")
                .deeplink(LinkConversionTestConfig.EXAMPLE_DEEPLINK)
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .shortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK)
                .build();
    }
}
