package com.sopromadze.blogapi.payload;

import javax.validation.constraints.NotBlank;

public class TagRequest {
    @NotBlank(message = "Tag name shouldn't blank")
    private String name;

    public String getName() {
        return name;
    }
}
