package com.sopromadze.blogapi.payload;

import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
	private String title;
	private String body;
	private String category;
	private List<String> tags;
}
