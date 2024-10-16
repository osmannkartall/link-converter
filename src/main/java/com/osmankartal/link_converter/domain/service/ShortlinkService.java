package com.osmankartal.link_converter.domain.service;

import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import com.osmankartal.link_converter.domain.model.Base62;
import com.osmankartal.link_converter.domain.model.IdGenerator;
import com.osmankartal.link_converter.domain.model.IdGeneratorProvider;
import com.osmankartal.link_converter.domain.model.LinkConversionConfig;
import org.springframework.stereotype.Service;


@Service
public class ShortlinkService {

    private final IdGenerator idGenerator;

    public ShortlinkService(IdGeneratorProvider idGeneratorProvider) {
        idGenerator = idGeneratorProvider.createIdGenerator();
    }

    public String createShortlink() {
        return LinkConversionConfig.SHORTLINK_DOMAIN + "/" + createShortlinkHash();
    }

    private String createShortlinkHash() {
        return Base62.encode(idGenerator.nextId());
    }

    public void validateShortlink(String shortlink) {
        if (!shortlink.startsWith(LinkConversionConfig.SHORTLINK_DOMAIN)) {
            throw new LinkConversionBusinessException("shortlink must start with " + LinkConversionConfig.SHORTLINK_DOMAIN);
        }
    }
}
