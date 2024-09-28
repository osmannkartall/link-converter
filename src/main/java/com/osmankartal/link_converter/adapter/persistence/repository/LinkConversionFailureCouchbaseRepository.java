package com.osmankartal.link_converter.adapter.persistence.repository;

import com.osmankartal.link_converter.adapter.persistence.document.LinkConversionFailureDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.couchbase.repository.CouchbaseRepository;
import org.springframework.stereotype.Repository;

@Profile("couchbase")
@Repository
public interface LinkConversionFailureCouchbaseRepository extends CouchbaseRepository<LinkConversionFailureDocument, String> {
}
