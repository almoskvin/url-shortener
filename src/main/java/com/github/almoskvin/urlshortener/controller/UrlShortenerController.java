package com.github.almoskvin.urlshortener.controller;

import com.github.almoskvin.urlshortener.AliasGeneratingException;
import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.service.UrlShortenerService;
import com.google.common.hash.Hashing;
import org.apache.commons.validator.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

@RestController
public class UrlShortenerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerController.class);
    private static final String DEFAULT_INSTANCE_URL = "http://localhost:8080/";

    private final UrlShortenerService urlShortenerService;

    private final Environment environment;

    @Autowired
    public UrlShortenerController(UrlShortenerService urlShortenerService, Environment environment) {
        this.urlShortenerService = urlShortenerService;
        this.environment = environment;
    }

    @GetMapping(value = "/{alias}")
    public ModelAndView redirect(@PathVariable String alias) throws ResourceNotFoundException {
        UrlLinker linker = urlShortenerService.findByAlias(alias);
        if (linker == null) {
            throw new ResourceNotFoundException();
        }
        //analytics
        linker.setLastTimeFollowed(new Date());
        Integer counter = linker.getFollowedTimesCounter();
        linker.setFollowedTimesCounter(++counter);
        urlShortenerService.save(linker);

        return new ModelAndView("redirect:" + linker.getLink());
    }

    @PostMapping(path = "/api/v1/urlLinker")
    public ResponseEntity<?> save(@RequestParam("link") String link) {
        if (isValidUrl(link)) {
            UrlLinker linker = urlShortenerService.findByLink(link);
            if (linker == null) {
                //save a new document so we can get a unique id of it
                linker = urlShortenerService.save(new UrlLinker(link));
                try {
                    //create an alias from the unique id provided by mongo
                    linker.setAlias(createAlias(linker.getId()));
                    linker = urlShortenerService.update(linker);
                } catch (AliasGeneratingException e) {
                    urlShortenerService.delete(linker);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot generate a short link", e);
                }
            }
            //TODO: not the full object
            return new ResponseEntity<>(linker, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //TODO: additional parameter 'projection=FULL' default NONE -> include analytics fields' values
    @GetMapping(path = "/api/v1/urlLinker")
    public ResponseEntity<?> expand(@RequestParam("alias") String alias) {
        UrlLinker linker = urlShortenerService.findByAlias(alias);
        if (linker == null) {
            LOGGER.trace("Cannot find a link for an alias {}", alias);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //TODO: not the full object
        return new ResponseEntity<>(linker, HttpStatus.FOUND);
    }

    @DeleteMapping("/api/v1/urlLinker")
    public ResponseEntity<?> delete(@RequestParam("alias") String alias) {
        urlShortenerService.deleteByAlias(alias);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates an ending for an alias for an incoming string via Google Guava hashing.<br/>
     * In case if the alias already exists, tries to create an alias for concatenation of
     * the previous ending and random substring of the incoming string.<br/>
     * If the ending is correct, forms a URL from concatenation of a value of the property {instance.url} and the generated ending
     * <br/>
     *
     * @param s source for an alias
     * @return String alias (https://localhost:8080/b4b65m3k)
     */
    private String createAlias(String s) {
        //using Google Guava hashing
        String alias = Hashing.murmur3_32().hashString(s, StandardCharsets.UTF_8).toString();
        if (urlShortenerService.existsByAlias(alias)) {
            Random random = new Random();
            alias = createAlias(alias + s.substring(random.nextInt(s.length())));
        }

        String instanceUrlPropertyValue = environment.getProperty("{instance.url}");
        String instanceUrl = instanceUrlPropertyValue == null ? DEFAULT_INSTANCE_URL : instanceUrlPropertyValue;
        alias = instanceUrl + alias;

        if (!isValidUrl(alias)) {
            throw new AliasGeneratingException("Generated link is invalid. Check {instance.url} property");
        }
        return alias;
    }

    private Boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url) || url.startsWith(DEFAULT_INSTANCE_URL);
    }
}
