package com.osmankartal.link_converter.adapter.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

@Entity
@Table(name = "link_conversion_failures")
@Builder
@Getter
@Profile("postgres-redis")
public class LinkConversionFailureJPAEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String endpoint;

    @NotBlank
    private String request;

    @Column(nullable = false)
    @NotBlank
    private String message;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
