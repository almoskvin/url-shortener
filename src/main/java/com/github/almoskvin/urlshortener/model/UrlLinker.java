package com.github.almoskvin.urlshortener.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class UrlLinker {

    @Id
    private String id;

    /**
     * Alias (short URL)
     */
    @Indexed(unique = true)
    private String alias;

    /**
     * Original link (long URL)
     */
    private String link;

    @CreatedDate
    private Date createdDate;

    private Date lastTimeFollowed;

    private Integer followedTimesCounter;

    public UrlLinker() {
        this.followedTimesCounter = 0;
    }

    public UrlLinker(String alias, String link) {
        this.alias = alias;
        this.link = link;
        this.followedTimesCounter = 0;
    }

    public UrlLinker(String link) {
        this.link = link;
        this.followedTimesCounter = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastTimeFollowed() {
        return lastTimeFollowed;
    }

    public void setLastTimeFollowed(Date lastTimeFollowed) {
        this.lastTimeFollowed = lastTimeFollowed;
    }

    public Integer getFollowedTimesCounter() {
        return followedTimesCounter;
    }

    public void setFollowedTimesCounter(Integer followedTimesCounter) {
        this.followedTimesCounter = followedTimesCounter;
    }
}
