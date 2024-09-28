package com.osmankartal.link_converter.adapter.persistence;

import com.osmankartal.link_converter.adapter.persistence.entity.LinkConversionJPAEntity;
import com.osmankartal.link_converter.adapter.persistence.repository.LinkConversionJPARepository;
import com.osmankartal.link_converter.core.port.LinkConversionPort;
import com.osmankartal.link_converter.domain.model.LinkConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Profile("postgres-redis")
@RequiredArgsConstructor
public class PostgresAndRedisAdapter implements LinkConversionPort {

    private final LinkConversionJPARepository linkConversionJPARepository;

    private final CacheManager cacheManager;

    @Override
    @CachePut(value = "link-conversion-cache",
            key = "#linkConversion.shortlink",
            condition = "#linkConversion.shortlink != null && #linkConversion.shortlink.isEmpty() != true")
    public LinkConversion save(LinkConversion linkConversion) {
        LinkConversion result = Optional.ofNullable(cacheManager)
                .map(cM -> cM.getCache("link-conversion-cache"))
                .map(cache -> cache.get(linkConversion.getShortlink(), LinkConversion.class))
                .filter(cachedLinkConversion -> cachedLinkConversion.equals(linkConversion))
                .orElse(null);

        if (Objects.isNull(result)) {
            result = linkConversionJPARepository.findByShortlink(linkConversion.getShortlink())
                    .map(LinkConversionJPAEntity::toModel)
                    .orElseGet(linkConversionJPARepository.save(LinkConversionJPAEntity.builder()
                            .id(Optional.ofNullable(linkConversion.getId()).map(Long::parseLong).orElse(null))
                            .url(linkConversion.getUrl())
                            .deeplink(linkConversion.getDeeplink())
                            .shortlink(linkConversion.getShortlink())
                            .createdAt(linkConversion.getCreatedAt())
                            .updatedAt(linkConversion.getUpdatedAt())
                            .build()
                    )::toModel);
        }

        return result;
    }

    @Override
    @Cacheable(value = "link-conversion-cache", key = "#shortlink")
    public Optional<LinkConversion> resolveShortlink(String shortlink) {
        return linkConversionJPARepository.findByShortlink(shortlink).map(LinkConversionJPAEntity::toModel);
    }

    @Override
    public void deleteAll() {
        linkConversionJPARepository.deleteAll();
    }

}
