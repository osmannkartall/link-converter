package com.osmankartal.link_converter.adapter.persistence.repository;

import com.osmankartal.link_converter.adapter.persistence.entity.LinkConversionFailureJPAEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("postgres-redis")
public interface LinkConversionFailureJPARepository extends JpaRepository<LinkConversionFailureJPAEntity, Long> {
}
