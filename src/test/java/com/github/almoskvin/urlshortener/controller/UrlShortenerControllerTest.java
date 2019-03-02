package com.github.almoskvin.urlshortener.controller;

import com.github.almoskvin.urlshortener.service.UrlShortenerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(value = UrlShortenerController.class, secure = false)
public class UrlShortenerControllerTest {

    //TODO: Write tests for Controller

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @Before
    public void setUp() {
    }

    @Test
    public void redirect() {
    }

    @Test
    public void save() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void expand() {
    }

    @Test
    public void createAlias() {
    }
}