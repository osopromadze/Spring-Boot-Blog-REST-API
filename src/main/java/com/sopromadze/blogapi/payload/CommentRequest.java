package com.sopromadze.blogapi.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentRequest {
	@NotBlank
	@Size(min = 10, message = "Comment body must be minimum 10 characters")
	private String body;
}
