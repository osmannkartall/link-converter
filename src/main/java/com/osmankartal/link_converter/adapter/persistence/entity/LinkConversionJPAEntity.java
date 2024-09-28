package com.osmankartal.link_converter.adapter.persistence.entity;


import com.osmankartal.link_converter.domain.model.LinkConversion;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.context.annotation.Profile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "link_conversions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Profile("postgres-redis")
public class LinkConversionJPAEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String url;

    @Column(name = "deeplink")
    @NotBlank
    private String deeplink;

    @Column(name = "shortlink", unique = true)
    @NotBlank
    private String shortlink;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LinkConversion toModel() {
        return LinkConversion.builder()
                .id(Objects.isNull(id) ? null : id.toString())
                .url(url)
                .deeplink(deeplink)
                .shortlink(shortlink)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}