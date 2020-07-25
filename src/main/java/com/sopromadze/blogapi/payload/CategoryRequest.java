package com.sopromadze.blogapi.payload;

import javax.validation.constraints.NotBlank;

public class CategoryRequest {
    @NotBlank(message = "Category name shouldn't blank")
    private String name;

    public String getName() {
        return name;
    }
}
