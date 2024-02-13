package com.sopromadze.blogapi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopromadze.blogapi.infrastructure.persistence.postgres.entity.TagEntity;
import com.sopromadze.blogapi.model.audit.UserDateAudit;
import com.sopromadze.blogapi.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

//@EqualsAndHashCode(callSuper = true)
//@Entity
@Data
//@Table(name = "posts", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post extends UserDateAudit {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "title")
  private String title;

  @Column(name = "body")
  private String body;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @JsonIgnore
  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Comment> comments;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "post_tag", joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
  private List<TagEntity> tags;

  @JsonIgnore
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Comment> getComments() {
    return comments == null ? null : new ArrayList<>(comments);
  }

  public void setComments(List<Comment> comments) {
    if (comments == null) {
      this.comments = null;
    } else {
      this.comments = Collections.unmodifiableList(comments);
    }
  }

  public List<TagEntity> getTags() {
    return tags == null ? null : new ArrayList<>(tags);
  }

  public void setTags(List<TagEntity> tags) {
    if (tags == null) {
      this.tags = null;
    } else {
      this.tags = Collections.unmodifiableList(tags);
    }
  }
}
