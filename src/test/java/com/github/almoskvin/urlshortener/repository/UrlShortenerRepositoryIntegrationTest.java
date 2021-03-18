package com.github.almoskvin.urlshortener.repository;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@DataMongoTest
public class UrlShortenerRepositoryIntegrationTest {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    private UrlLinker linker1;
    private UrlLinker linker2;

    @BeforeEach
    public void setUp() {
        linker1 = new UrlLinker("alias1", "link1");
        linker2 = new UrlLinker("alias2", "link2");
        urlShortenerRepository.save(linker1);
        urlShortenerRepository.save(linker2);
    }

    @Test
    public void testSave() {
        assertNotNull(linker1.getId());
        assertNotNull(linker2.getId());
    }

    @Test
    public void testFindByAlias() {
        assertEquals("link1", urlShortenerRepository.findByAlias("alias1").getLink());
        assertEquals("link2", urlShortenerRepository.findByAlias("alias2").getLink());
    }

    @Test
    public void testFindFirstByLink() {
        assertNotNull(urlShortenerRepository.findFirstByLink("link1"));
        assertNotNull(urlShortenerRepository.findFirstByLink("link2"));
    }

    @Test
    public void testDeleteUrlLinkerByAlias() {
        urlShortenerRepository.deleteUrlLinkerByAlias("alias2");
        assertNull(urlShortenerRepository.findByAlias("alias2"));
        assertNotNull(urlShortenerRepository.findByAlias("alias1"));
    }

    @AfterEach
    public void tearDown() {
        urlShortenerRepository.delete(linker1);
        urlShortenerRepository.delete(linker2);
    }
}