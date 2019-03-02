package com.github.almoskvin.urlshortener.service;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.repository.UrlShortenerRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UrlShortenerServiceTest {

    @Mock
    private UrlShortenerRepository urlShortenerRepository;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    UrlLinker mockLinker;

    @Before
    public void setUp() throws Exception {
        mockLinker = new UrlLinker("testAlias", "testLink");
    }

    @Test
    public void testGetLinkByAliasWhenLinkerExists() {
        when(urlShortenerRepository.findByAlias("testAlias")).thenReturn(mockLinker);

        assertEquals("testLink", urlShortenerService.getLinkByAlias("testAlias"));
    }

    @Test
    public void testGetLinkByAliasWhenLinkerDoesNotExist() {
        when(urlShortenerRepository.findByAlias(anyString())).thenReturn(null);

        assertNull(urlShortenerService.getLinkByAlias("testAlias"));
    }

    @Test
    public void testFindByLinkWhenLinkIsNotNull() {
        when(urlShortenerRepository.findFirstByLink(anyString())).thenReturn(mockLinker);
        assertEquals(mockLinker, urlShortenerService.findByLink("testLink"));
    }

    @Test
    public void testFindByLinkWhenLinkIsNull() {
        when(urlShortenerRepository.findFirstByLink(anyString())).thenReturn(null);
        assertNull(urlShortenerService.findByLink("testLink"));
    }

    @Test
    public void testSave() {
        when(urlShortenerRepository.save(any(UrlLinker.class))).thenReturn(mockLinker);
        assertEquals(mockLinker, urlShortenerService.save(mockLinker));
    }

    @Test
    public void testDeleteByAlias() {
        //do nothing
    }

    @Test
    public void testFindByAliasWhenLinkerExists() {
        when(urlShortenerRepository.findByAlias(anyString())).thenReturn(mockLinker);
        assertEquals(mockLinker, urlShortenerService.findByAlias("testAlias"));
    }

    @Test
    public void testFindByAliasWhenLinkerDoesNotExist() {
        when(urlShortenerRepository.findByAlias(anyString())).thenReturn(null);
        assertNull(urlShortenerService.findByAlias("wrongAlias"));
    }
}