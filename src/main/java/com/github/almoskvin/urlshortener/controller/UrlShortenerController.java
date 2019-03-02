package com.github.almoskvin.urlshortener.controller;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.service.UrlShortenerService;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

@RestController
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
        UrlLinker linker = urlShortenerService.findByAlias(alias);
        if (linker == null) {
            throw new ResourceNotFoundException();
        }
        //analytics
        linker.setLastTimeFollowed(new Date());
        Integer counter = linker.getFollowedTimesCounter();
        linker.setFollowedTimesCounter(counter == null ? 0 : ++counter);
        urlShortenerService.save(linker);

        return new ModelAndView("redirect:" + linker.getLink());
    }

    @PostMapping(path = "/api/v1/urlLinker")
    public ResponseEntity<?> save(@RequestParam("link") String link) {
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(link)) {
            UrlLinker linker = urlShortenerService.findByLink(link);
            if (linker == null) {
                //save a new document so we can get a unique id of it
                linker = urlShortenerService.save(new UrlLinker(link));
                //create an alias from the unique id provided by mongo
                linker.setAlias(createAlias(linker.getId()));
                //update the document
                linker = urlShortenerService.save(linker);
            }
            return new ResponseEntity<>(linker, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/api/v1/urlLinker")
    public ResponseEntity<?> expand(@RequestParam("alias") String alias) {
        UrlLinker linker = urlShortenerService.findByAlias(alias);
        if (linker == null) {
            LOGGER.trace("Cannot find a link for an alias {}", alias);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(linker, HttpStatus.FOUND);
    }

    @DeleteMapping("/api/v1/urlLinker")
    public ResponseEntity<?> delete(@RequestParam("alias") String alias) {
        try {
            urlShortenerService.deleteByAlias(alias);
        } catch (Exception e) {
            LOGGER.error("Cannot delete record by alias " + alias, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates an alias for the incoming string via Google Guava hashing.<br/>
     * In case if the alias already exists, tries to create an alias for concatenation of
     * the previous alias and random substring of the incoming string.<br/>
     * <br/>
     * For example, for s == "5c7abd7df7e87c53b4fcd613" the method will return an alias like this: "6c7ba25a", if no such record found in a database
     *
     * @param s source for an alias
     * @return String alias
     */
    private String createAlias(String s) {
        //using Google Guava hashing
        String alias = Hashing.murmur3_32().hashString(s, StandardCharsets.UTF_8).toString();
        if (urlShortenerService.existsByAlias(alias)) {
            Random random = new Random();
            alias = createAlias(alias + s.substring(random.nextInt(s.length())));
        }
        return alias;
    }
}
