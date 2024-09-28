package com.osmankartal.link_converter.adapter.persistence.document;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Profile("couchbase")
public class LinkConversionFailureDocument {

    @Id
    @GeneratedValue(strategy = UNIQUE)
    private String id;

    @NotBlank
    private String endpoint;

    @NotBlank
    private String request;

    @NotBlank
    private String message;

    @Field("_created")
    private String created;
}
