package com.sopromadze.blogapi.model.todo;

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
import com.sopromadze.blogapi.model.audit.UserDateAudit;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.TodoRequest;

@Entity
@Table(name = "todos", uniqueConstraints = { @UniqueConstraint(columnNames = { "title" }) })
public class Todo extends UserDateAudit {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(name = "title")
	private String title;

	@Column(name = "completed")
	private Boolean completed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public Todo(TodoRequest todoRequest) {
		title = todoRequest.getTitle();
		completed = todoRequest.isCompleted();
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

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	@JsonIgnore
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
