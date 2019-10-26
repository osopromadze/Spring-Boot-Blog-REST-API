package com.sopromadze.blogapi.model.photo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopromadze.blogapi.model.album.Album;
import com.sopromadze.blogapi.model.audit.UserDateAudit;

@Entity
@Table(name = "photos", uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Photo extends UserDateAudit {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "title")
	private String title;

	@NotBlank
	@Column(name = "url")
	private String url;

	@NotBlank
	@Column(name = "thumbnail_url")
	private String thumbnailUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "album_id")
	private Album album;

	public Photo() {

	}

	public Photo(@NotBlank String title, @NotBlank String url, @NotBlank String thumbnailUrl, Album album) {
		this.title = title;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
		this.album = album;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	@JsonIgnore
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
}
