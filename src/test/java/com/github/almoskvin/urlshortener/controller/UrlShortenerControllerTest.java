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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(value = UrlShortenerController.class, secure = false)
public class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlShortenerController urlShortenerController;

    @Before
    public void setUp() throws Exception {
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
    public void encode() {
        System.out.println(urlShortenerController.encode("https://www.springboottutorial.com/unit-testing-for-spring-boot-rest-services"));
    }
}