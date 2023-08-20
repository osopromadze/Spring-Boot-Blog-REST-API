package com.sopromadze.blogapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank
    @Size(min = 10, message = "Comment body must be minimum 10 characters")
    private String body;
}
