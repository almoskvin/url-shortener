package com.github.almoskvin.urlshortener.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document
public class UrlLinker {

    @Id
    private String id;

    /**
     * Alias (short URL)
     */
    @Indexed(unique = true)
    @NotNull(message = "Alias for URL must not be null")
    private String alias;

    /**
     * Original link
     */
    @NotNull(message = "URL must not be null")
    private String link;

    @CreatedDate
    private Date createdDate;

    public UrlLinker(String alias, String link) {
        this.alias = alias;
        this.link = link;
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
}
