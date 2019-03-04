package com.github.almoskvin.urlshortener.controller;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.service.UrlShortenerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = UrlShortenerController.class)
public class UrlShortenerControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    private UrlLinker newMockLinker;
    private UrlLinker savedMockLinker;
    private UrlLinker filledMockLinker;

    @Before
    public void setUp() {
        newMockLinker = new UrlLinker("https://valid.link/");
        //after initial save
        savedMockLinker = newMockLinker;
        savedMockLinker.setId("testId");
        //after updating with an alias
        filledMockLinker = savedMockLinker;
        filledMockLinker.setAlias("testAlias");
    }

    @Test
    public void testRedirectWhenLinkerDoesNotExist() throws Exception {
        given(urlShortenerService.findByAlias("testAlias")).willReturn(null);
        mockMvc.perform(get("/testAlias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testRedirectWhenLinkerExists() throws Exception {
        assertNull(filledMockLinker.getLastTimeFollowed());
        assertEquals(0, (int) filledMockLinker.getFollowedTimesCounter());

        given(urlShortenerService.findByAlias("testAlias")).willReturn(filledMockLinker);
        mockMvc.perform(get("/testAlias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://valid.link/"));

        assertNotNull(filledMockLinker.getLastTimeFollowed());
        assertEquals(1, (int) filledMockLinker.getFollowedTimesCounter());

        ArgumentCaptor<UrlLinker> argumentCaptor = ArgumentCaptor.forClass(UrlLinker.class);
        verify(urlShortenerService).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(filledMockLinker));
    }

    @Test
    public void testSaveWhenLinkIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/urlLinker")
                .param("link", "invalidLink")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyZeroInteractions(urlShortenerService);
    }

    @Test
    public void testSaveWhenLinkIsValidAndLinkerDoesNotExist() throws Exception {
        given(urlShortenerService.findByLink(anyString())).willReturn(null);
        given(urlShortenerService.save(ArgumentMatchers.any(UrlLinker.class))).willReturn(savedMockLinker);
        given(urlShortenerService.update(ArgumentMatchers.any(UrlLinker.class))).willReturn(filledMockLinker);

        mockMvc.perform(post("/api/v1/urlLinker")
                .param("link", "https://valid.link/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").value("https://valid.link/"))
                .andExpect(jsonPath("$.alias").isNotEmpty());

        verify(urlShortenerService, times(1)).findByLink("https://valid.link/");

        ArgumentCaptor<UrlLinker> argumentCaptor = ArgumentCaptor.forClass(UrlLinker.class);

        verify(urlShortenerService).save(argumentCaptor.capture());
        assertEquals(argumentCaptor.getValue().getLink(), "https://valid.link/");
        assertNull(argumentCaptor.getValue().getId());
        assertNull(argumentCaptor.getValue().getAlias());

        verify(urlShortenerService).update((argumentCaptor.capture()));
        assertNotNull(argumentCaptor.getValue().getAlias());
    }

    @Test
    public void testSaveWhenLinkIsValidAndLinkerExists() throws Exception {
        given(urlShortenerService.findByLink(anyString())).willReturn(filledMockLinker);

        mockMvc.perform(post("/api/v1/urlLinker")
                .param("link", "https://valid.link/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.link").value("https://valid.link/"))
                .andExpect(jsonPath("$.alias").isNotEmpty());

        verify(urlShortenerService, times(1)).findByLink("https://valid.link/");
        verifyNoMoreInteractions(urlShortenerService);
    }

    @Test
    public void testDelete() throws Exception {
        doNothing().when(urlShortenerService).deleteByAlias(anyString());

        mockMvc.perform(delete("/api/v1/urlLinker")
                .param("alias", "testAlias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(urlShortenerService, times(1)).deleteByAlias(anyString());
    }

    @Test
    public void testExpandWhenLinkerExists() throws Exception {
        given(urlShortenerService.findByAlias(anyString())).willReturn(filledMockLinker);

        mockMvc.perform(get("/api/v1/urlLinker")
                .param("alias", "testAlias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound())
                .andExpect(jsonPath("$.link").value("https://valid.link/"))
                .andExpect(jsonPath("$.alias").value("testAlias"));

        verify(urlShortenerService, times(1)).findByAlias(anyString());
    }

    @Test
    public void testExpandWhenLinkerDoesNotExist() throws Exception {
        given(urlShortenerService.findByAlias(anyString())).willReturn(null);

        mockMvc.perform(get("/api/v1/urlLinker")
                .param("alias", "testAlias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(urlShortenerService, times(1)).findByAlias(anyString());
    }
}