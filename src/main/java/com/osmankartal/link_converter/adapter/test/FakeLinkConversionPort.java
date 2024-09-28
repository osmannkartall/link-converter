package com.osmankartal.link_converter.adapter.test;

import com.osmankartal.link_converter.core.port.LinkConversionPort;
import com.osmankartal.link_converter.domain.model.LinkConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FakeLinkConversionPort implements LinkConversionPort {

    private final List<LinkConversion> linkConversions = new ArrayList<>();

    @Override
    public Optional<LinkConversion> resolveShortlink(String shortlink) {
        return linkConversions.stream()
                .filter(linkConversion -> linkConversion.getShortlink().equals(shortlink))
                .findFirst();
    }

    @Override
    public LinkConversion save(LinkConversion linkConversion) {
        linkConversions.add(linkConversion);
        return linkConversion;
    }

    @Override
    public void deleteAll() {
        linkConversions.clear();
    }

}
