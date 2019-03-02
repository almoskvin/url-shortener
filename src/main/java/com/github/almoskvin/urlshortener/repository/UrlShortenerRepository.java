package com.github.almoskvin.urlshortener.repository;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends MongoRepository<UrlLinker, String> {

    UrlLinker findByAlias(String s);

    UrlLinker findFirstByLink(String s);

    void deleteUrlLinkerByAlias(String s);

    Boolean existsByAlias(String s);
}
