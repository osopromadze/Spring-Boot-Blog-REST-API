package com.sopromadze.blogapi.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sopromadze.blogapi.model.photo.Photo;
import com.sopromadze.blogapi.model.user.User;

@JsonInclude(Include.NON_NULL)
public class AlbumResponse extends UserDateAuditPayload {
	private Long id;

	private String title;

	private User user;

	private List<Photo> photo;

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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
