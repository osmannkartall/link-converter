package com.osmankartal.link_converter.domain.model;

import com.osmankartal.link_converter.domain.exception.LinkConversionBusinessException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@EqualsAndHashCode
@Builder
public class LinkConversion implements Serializable {

    private String id;
    private String url;
    private String deeplink;
    private String shortlink;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LinkConversion(String id, String url, String deeplink, String shortlink, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.url = url;
        this.deeplink = deeplink;
        this.shortlink = shortlink;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        validateDeeplink();
        validateUrl();
    }

    public void validateDeeplink() {
        if (Objects.nonNull(deeplink) && !deeplink.trim().startsWith(LinkConversionConfig.BASE_DEEPLINK)) {
            throw new LinkConversionBusinessException("deeplink must start with " + LinkConversionConfig.BASE_DEEPLINK);
        }
    }

    public void validateUrl() {
        if (Objects.nonNull(url) && !url.startsWith(LinkConversionConfig.BASE_URL)) {
            throw new LinkConversionBusinessException("url must start with " + LinkConversionConfig.BASE_URL);
        }
    }

}
