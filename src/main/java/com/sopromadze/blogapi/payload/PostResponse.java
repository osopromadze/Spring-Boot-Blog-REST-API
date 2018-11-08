package com.sopromadze.blogapi.payload;

import java.time.Instant;

public class PostResponse {
    private Long id;
    private String title;
    private String body;
    private UserSummary createdBy;
    private Instant updatedDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UserSummary getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSummary createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Instant updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
