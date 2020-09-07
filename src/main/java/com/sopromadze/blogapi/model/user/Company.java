package com.sopromadze.blogapi.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopromadze.blogapi.model.audit.UserDateAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(name = "company")
public class Company extends UserDateAudit {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "catch_phrase")
	private String catchPhrase;

	@Column(name = "bs")
	private String bs;

	@OneToOne(mappedBy = "company")
	private User user;


	public Company(String name, String catchPhrase, String bs) {
		this.name = name;
		this.catchPhrase = catchPhrase;
		this.bs = bs;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	@Override
	public Long getCreatedBy() {
		return super.getCreatedBy();
	}

	@JsonIgnore
	@Override
	public void setCreatedBy(Long createdBy) {
		super.setCreatedBy(createdBy);
	}

	@JsonIgnore
	@Override
	public Long getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@JsonIgnore
	@Override
	public void setUpdatedBy(Long updatedBy) {
		super.setUpdatedBy(updatedBy);
	}

	@JsonIgnore
	@Override
	public Instant getCreatedAt() {
		return super.getCreatedAt();
	}

	@JsonIgnore
	@Override
	public void setCreatedAt(Instant createdAt) {
		super.setCreatedAt(createdAt);
	}

	@JsonIgnore
	@Override
	public Instant getUpdatedAt() {
		return super.getUpdatedAt();
	}

	@JsonIgnore
	@Override
	public void setUpdatedAt(Instant updatedAt) {
		super.setUpdatedAt(updatedAt);
	}
}
