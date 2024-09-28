package com.osmankartal.link_converter.core.port;


import com.osmankartal.link_converter.domain.model.LinkConversion;

import java.util.Optional;


public interface LinkConversionPort {
    LinkConversion save(LinkConversion linkConversion);

    Optional<LinkConversion> resolveShortlink(String shortlink);

    void deleteAll();
}
