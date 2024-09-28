package com.osmankartal.link_converter.adapter.persistence.repository;

import com.osmankartal.link_converter.adapter.persistence.document.LinkConversionDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Profile("couchbase")
@Repository
public interface LinkConversionCouchbaseRepository extends CouchbaseRepository<LinkConversionDocument, String> {
    Optional<LinkConversionDocument> findByShortlink(String shortlink);
}
