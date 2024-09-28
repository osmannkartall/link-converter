package com.osmankartal.link_converter.adapter.test;

import com.osmankartal.link_converter.core.command.CreateShortlinkCommand;
import com.osmankartal.link_converter.core.handler.Handler;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import org.springframework.stereotype.Service;


@Service
public class FakeCreateShortlinkHandler implements Handler<CreateShortlinkCommand, LinkConversion> {

    @Override
    public LinkConversion execute(CreateShortlinkCommand command) {
        return LinkConversion.builder()
                .id("1")
                .deeplink(LinkConversionTestConfig.EXAMPLE_DEEPLINK)
                .url(LinkConversionTestConfig.EXAMPLE_URL)
                .shortlink(LinkConversionTestConfig.EXAMPLE_SHORTLINK)
                .build();
    }
}
