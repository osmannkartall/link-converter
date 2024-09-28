package com.osmankartal.link_converter.adapter.persistence.document;

import com.osmankartal.link_converter.domain.model.LinkConversion;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Profile("couchbase")
public class LinkConversionDocument implements Serializable {

    @Id
    @GeneratedValue(strategy = UNIQUE)
    private String id;

    @Field
    @NotNull
    private String url;

    @Field
    private String deeplink;

    @Field
    @QueryIndexed
    @NotNull
    private String shortlink;

    @Field("_created")
    private String createdAt;

    @Field("_updated")
    private String updatedAt;

    public LinkConversion toModel() {
        return LinkConversion.builder()
                .id(id)
                .url(url)
                .deeplink(deeplink)
                .shortlink(shortlink)
                .createdAt(Objects.isNull(createdAt) ? null : LocalDateTime.parse(createdAt))
                .updatedAt(Objects.isNull(updatedAt) ? null : LocalDateTime.parse(updatedAt))
                .build();
    }
}