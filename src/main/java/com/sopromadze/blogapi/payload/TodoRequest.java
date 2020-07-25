package com.sopromadze.blogapi.payload;

import javax.validation.constraints.NotBlank;

public class TodoRequest {

    @NotBlank(message = "Todo title shouldn't blank")
    private String title;
    private boolean completed;

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }
}
