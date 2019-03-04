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
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

@RestController
public class UrlShortenerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerController.class);

    static final String DEFAULT_INSTANCE_URL = "http://localhost:8080/";
    static final String PROJECTION_FULL = "FULL";
    static final String PROJECTION_NONE = "NONE";

    private final UrlShortenerService urlShortenerService;

    private final Environment environment;

    @Autowired
    public UrlShortenerController(UrlShortenerService urlShortenerService, Environment environment) {
        this.urlShortenerService = urlShortenerService;
        this.environment = environment;
    }

    @GetMapping(value = "/{alias}")
    public ModelAndView redirect(@PathVariable String alias) throws ResourceNotFoundException {
        UrlLinker linker = urlShortenerService.findByAlias(getInstanceUrl() + alias);
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
    public ResponseEntity<?> save(@RequestBody UrlLinker initialLinker) {
        String link = initialLinker.getLink();
        if (isValidUrl(link)) {
            UrlLinker linker = urlShortenerService.findByLink(link);
            if (linker == null) {
                //save a new document so we can get a unique id of it
                linker = urlShortenerService.save(new UrlLinker(link));
                //unexpected troubles with the CreatedDate annotation
                linker.setCreatedDate(new Date());
                try {
                    //create an alias from the unique id provided by mongo
                    linker.setAlias(createAlias(linker.getId()));
                    linker = urlShortenerService.update(linker);
                } catch (AliasGeneratingException e) {
                    urlShortenerService.delete(linker);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot generate a short link", e);
                }
            }
            Map<String, Object> responseMap = new TreeMap<>();
            responseMap.put("alias", linker.getAlias());
            responseMap.put("link", linker.getLink());
            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/api/v1/urlLinker")
    public ResponseEntity<?> expand(@RequestParam("alias") String alias,
                                    @RequestParam(value = "projection", required = false, defaultValue = PROJECTION_NONE) String projection) {
        UrlLinker linker = urlShortenerService.findByAlias(alias);
        if (linker == null) {
            LOGGER.trace("Cannot find a link for an alias {}", alias);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Map<String, Object> responseMap = new TreeMap<>();
        responseMap.put("alias", linker.getAlias());
        responseMap.put("link", linker.getLink());

        //analytics
        if (PROJECTION_FULL.equals(projection)) {
            Map<String, Object> analyticsMap = new TreeMap<>();
            analyticsMap.put("created", linker.getCreatedDate());
            analyticsMap.put("lastTimeFollowed", linker.getLastTimeFollowed());
            analyticsMap.put("followedTimes", linker.getFollowedTimesCounter());
            responseMap.put("analytics", analyticsMap);
        }
        return new ResponseEntity<>(responseMap, HttpStatus.FOUND);
    }

    @DeleteMapping("/api/v1/urlLinker")
    public ResponseEntity<?> delete(@RequestParam("alias") String alias) {
        if (urlShortenerService.existsByAlias(alias)) {
            urlShortenerService.deleteByAlias(alias);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
        } else {
            alias = getInstanceUrl() + alias;
        }

        if (!isValidUrl(alias)) {
            LOGGER.error("Generated URL {} is invalid", alias);
            throw new AliasGeneratingException("Generated link is invalid. Check {instance.url} property");
        }
        return alias;
    }

    private String getInstanceUrl() {
        String instanceUrlPropertyValue = environment.getProperty("{instance.url}");
        return instanceUrlPropertyValue == null ? DEFAULT_INSTANCE_URL : instanceUrlPropertyValue;
    }

    private Boolean isValidUrl(String url) {
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url) || url.startsWith(DEFAULT_INSTANCE_URL);
    }
}
