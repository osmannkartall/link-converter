package com.osmankartal.link_converter.adapter.persistence.repository;

import com.osmankartal.link_converter.adapter.persistence.entity.LinkConversionJPAEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("postgres-redis")
public interface LinkConversionJPARepository extends JpaRepository<LinkConversionJPAEntity, String> {
    Optional<LinkConversionJPAEntity> findByShortlink(String shortlink);
}
