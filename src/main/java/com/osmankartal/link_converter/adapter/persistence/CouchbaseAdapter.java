package com.osmankartal.link_converter.adapter.persistence;

import com.osmankartal.link_converter.adapter.persistence.document.LinkConversionDocument;
import com.osmankartal.link_converter.adapter.persistence.repository.LinkConversionCouchbaseRepository;
import com.osmankartal.link_converter.core.port.LinkConversionPort;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Profile("couchbase")
@RequiredArgsConstructor
public class CouchbaseAdapter implements LinkConversionPort {

    private final LinkConversionCouchbaseRepository linkConversionCouchbaseRepository;

    private final CouchbaseTemplate couchbaseTemplate;

    @Override
    @CachePut(value = "link-conversion-cache",
            key = "#linkConversion.shortlink",
            condition = "#linkConversion.shortlink != null && !#linkConversion.shortlink.isEmpty()")
    public LinkConversion save(LinkConversion linkConversion) {
        LinkConversionDocument linkConversionDocument = LinkConversionDocument.builder()
                .id(linkConversion.getId())
                .url(linkConversion.getUrl())
                .deeplink(linkConversion.getDeeplink())
                .shortlink(linkConversion.getShortlink())
                .createdAt(Objects.isNull(linkConversion.getCreatedAt()) ? null : linkConversion.getCreatedAt().toString())
                .updatedAt(Objects.isNull(linkConversion.getUpdatedAt()) ? null : linkConversion.getUpdatedAt().toString())
                .build();

        couchbaseTemplate.upsertById(LinkConversionDocument.class).one(linkConversionDocument);

        return linkConversion;
    }

    @Override
    @Cacheable(value = "link-conversion-cache", key = "#shortlink")
    public Optional<LinkConversion> resolveShortlink(String shortlink) {
        return linkConversionCouchbaseRepository.findByShortlink(shortlink).map(LinkConversionDocument::toModel);
    }

    @Override
    public void deleteAll() {
        linkConversionCouchbaseRepository.deleteAll();
    }
}
