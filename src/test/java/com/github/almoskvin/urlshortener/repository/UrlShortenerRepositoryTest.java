package com.github.almoskvin.urlshortener.repository;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UrlShortenerRepositoryTest {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    UrlLinker linker1;
    UrlLinker linker2;

    @Before
    public void setUp() throws Exception {
        linker1 = new UrlLinker("alias1", "link1");
        linker2 = new UrlLinker("alias2", "link2");
    }

    @Test
    public void testSave() {
        assertNull(linker1.getId());
        assertNull(linker2.getId());
        urlShortenerRepository.save(linker1);
        urlShortenerRepository.save(linker2);
        assertNotNull(linker1);
        assertNotNull(linker2);
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

    @After
    public void tearDown() throws Exception {
        urlShortenerRepository.delete(linker1);
        urlShortenerRepository.delete(linker2);
    }
}