package com.github.almoskvin.urlshortener.controller;

import com.github.almoskvin.urlshortener.model.UrlLinker;
import com.github.almoskvin.urlshortener.service.UrlShortenerService;
import com.twitter.bijection.codec.Base64;
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

@RestController
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
    @PostMapping(path = "/url")
    public ResponseEntity<?> save(@RequestParam("link") String link) {
        UrlValidator urlValidator = new UrlValidator();
        if (urlValidator.isValid(link)) {
            UrlLinker linker = urlShortenerService.findByLink(link);
            if (linker == null) {
                //TODO: alias
//                final String id = Hashing.murmur3_32().hashString(link, StandardCharsets.UTF_8).toString();
                String alias = "";
                linker = urlShortenerService.save(new UrlLinker(alias, link));
            }
            return new ResponseEntity<>(linker, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path = "/url")
    public ResponseEntity<?> expand(@RequestParam("alias") String alias) {
        UrlLinker linker = urlShortenerService.findByAlias(alias);
        if (linker == null) {
            LOGGER.trace("Cannot find a link for an alias {}", alias);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<UrlLinker>(linker, HttpStatus.FOUND);
    }

    @DeleteMapping("/url")
    public ResponseEntity<?> delete(@RequestParam("alias") String alias) {
        try {
            urlShortenerService.deleteByAlias(alias);
        } catch (Exception e) {
            LOGGER.error("Cannot delete record by alias " + alias, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //TODO:shorten an encoded outcome
    public String encode(String link) {
        Base64 codec = new Base64();
        return codec.encodeBase64URLSafeString(link.getBytes(StandardCharsets.UTF_8));
    }
}
