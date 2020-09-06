package com.sopromadze.blogapi.payload;

import lombok.Data;

@Data
public class PhotoResponse {
	private Long id;
	private String title;
	private String url;
	private String thumbnailUrl;
	private Long albumId;

	public PhotoResponse(Long id, String title, String url, String thumbnailUrl, Long albumId) {
		this.id = id;
		this.title = title;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
		this.albumId = albumId;
	}

}
