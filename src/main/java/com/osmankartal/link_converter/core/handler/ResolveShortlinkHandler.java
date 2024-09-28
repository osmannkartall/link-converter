package com.osmankartal.link_converter.core.handler;

import com.osmankartal.link_converter.core.command.ResolveShortlinkCommand;
import com.osmankartal.link_converter.core.port.LinkConversionPort;
import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.exception.LinkConversionNotFoundException;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import com.osmankartal.link_converter.domain.service.ShortlinkService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@AllArgsConstructor
@Service
public class ResolveShortlinkHandler implements Handler<ResolveShortlinkCommand, LinkConversion> {

    private final LinkConversionPort linkConversionPort;
    private final ShortlinkService shortlinkService;

    public LinkConversion execute(ResolveShortlinkCommand resolveShortlinkCommand) {
        if (Objects.isNull(resolveShortlinkCommand)) {
            throw new LinkConversionBusinessException("resolve shortlink command error");
        }

        shortlinkService.validateShortlink(resolveShortlinkCommand.getShortlink());

        return linkConversionPort.resolveShortlink(resolveShortlinkCommand.getShortlink())
                .orElseThrow(() -> new LinkConversionNotFoundException("Cannot find the record for the given shortlink", resolveShortlinkCommand.getShortlink()));
    }

}
