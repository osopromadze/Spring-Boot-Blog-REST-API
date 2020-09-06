package com.sopromadze.blogapi.payload.request;

import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserDateAuditPayload;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class AlbumRequest extends UserDateAuditPayload {

	private Long id;

	private String title;

	private User user;

	private List<Photo> photo;

	public List<Photo> getPhoto() {

		return photo == null ? null : new ArrayList<>(photo);
	}

	public void setPhoto(List<Photo> photo) {

		if (photo == null) {
			this.photo = null;
		} else {
			this.photo = Collections.unmodifiableList(photo);
		}
	}
}
