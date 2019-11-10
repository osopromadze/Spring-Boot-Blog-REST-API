package com.sopromadze.blogapi.payload.request;

import java.util.ArrayList;
import java.util.List;

import com.sopromadze.blogapi.model.photo.Photo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserDateAuditPayload;

public class AlbumRequest extends UserDateAuditPayload {

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

	public List<Photo> getPhoto() {
		return photo;
	}

	public void setPhoto(List<Photo> photo) {
		if (photo != null) {
			this.photo = new ArrayList<>(photo);
		} else {
			this.photo = null;
		}
	}
}
