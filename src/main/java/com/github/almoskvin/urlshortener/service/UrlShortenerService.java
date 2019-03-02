package com.github.almoskvin.urlshortener.service;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UrlShortenerService {

    private final UrlShortenerRepository urlShortenerRepository;

    @Autowired
    public UrlShortenerService(UrlShortenerRepository urlShortenerRepository) {
        this.urlShortenerRepository = urlShortenerRepository;
    }

    @Transactional(readOnly = true)
    public String getLinkByAlias(String alias) {
        UrlLinker linker = urlShortenerRepository.findByAlias(alias);
        return linker == null ? null : linker.getLink();
    }

    @Transactional(readOnly = true)
    public UrlLinker findByLink(String link) {
        return urlShortenerRepository.findFirstByLink(link);
    }

    public UrlLinker save(UrlLinker linker) {
        return urlShortenerRepository.save(linker);
    }

    public void deleteByAlias(String alias) {
        urlShortenerRepository.deleteUrlLinkerByAlias(alias);
    }

    public UrlLinker findByAlias(String alias) {
        return urlShortenerRepository.findByAlias(alias);
    }
}
