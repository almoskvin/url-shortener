package com.github.almoskvin.urlshortener.controller;

import com.github.almoskvin.urlshortener.service.UrlShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class UrlShortenerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerController.class);

    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /*@RequestMapping(value = "/{alias}", method = RequestMethod.GET)
    public void redirect(@PathVariable String alias, HttpServletResponse response) throws Exception {
        final String link = urlShortenerService.getLinkByAlias(alias);

        if (link != null) {
            response.sendRedirect(link);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }*/

    @GetMapping(value = "/{alias}")
    public ModelAndView redirect(@PathVariable String alias) throws ResourceNotFoundException {
        final String link = urlShortenerService.getLinkByAlias(alias);

        if (link == null) {
            throw new ResourceNotFoundException();
        }
        return new ModelAndView("redirect:" + link);
    }

    //TODO: post mapping (API call)
}
