package com.github.almoskvin.urlshortener.service;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerService {

    private final UrlShortenerRepository urlShortenerRepository;

    @Autowired
    public UrlShortenerService(UrlShortenerRepository urlShortenerRepository) {
        this.urlShortenerRepository = urlShortenerRepository;
    }

    public String getLinkByAlias(String shortUrl) {
        UrlLinker linker = urlShortenerRepository.findByAlias(shortUrl);
        return linker == null ? null : linker.getLink();
    }
}
