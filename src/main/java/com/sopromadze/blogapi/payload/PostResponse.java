package com.sopromadze.blogapi.payload;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class PostResponse {
	private String title;
	private String body;
	private String category;
	private List<String> tags;



	public List<String> getTags() {

		return tags == null ? null : new ArrayList<>(tags);
	}

	public void setTags(List<String> tags) {

		if (tags == null) {
			this.tags = null;
		} else {
			this.tags = Collections.unmodifiableList(tags);
		}
	}
}
