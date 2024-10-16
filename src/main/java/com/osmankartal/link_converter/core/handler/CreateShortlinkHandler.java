package com.osmankartal.link_converter.core.handler;

import com.osmankartal.link_converter.core.command.CreateShortlinkCommand;
import com.osmankartal.link_converter.core.port.LinkConversionPort;
import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import com.osmankartal.link_converter.domain.service.ShortlinkService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;


@AllArgsConstructor
@Service
public class CreateShortlinkHandler implements Handler<CreateShortlinkCommand, LinkConversion> {

    private final LinkConversionPort linkConversionPort;

    private final ShortlinkService shortlinkService;

    public LinkConversion execute(CreateShortlinkCommand createShortlinkCommand) {
        if (Objects.isNull(createShortlinkCommand)) {
            throw new LinkConversionBusinessException("create shortlink command error");
        }

        LocalDateTime now = LocalDateTime.now();

        return linkConversionPort.save(LinkConversion.builder()
                .url(createShortlinkCommand.getUrl())
                .deeplink(createShortlinkCommand.getDeeplink())
                .shortlink(shortlinkService.createShortlink())
                .createdAt(now)
                .updatedAt(now)
                .build());
    }

}
