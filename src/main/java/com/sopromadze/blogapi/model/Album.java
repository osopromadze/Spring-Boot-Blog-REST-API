package com.sopromadze.blogapi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopromadze.blogapi.model.audit.UserDateAudit;
import com.sopromadze.blogapi.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "albums", uniqueConstraints = {@UniqueConstraint(columnNames = {"title"})})
public class Album extends UserDateAudit {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photo;

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public List<Photo> getPhoto() {
        return this.photo == null ? null : new ArrayList<>(this.photo);
    }

    public void setPhoto(List<Photo> photo) {
        if (photo == null) {
            this.photo = null;
        } else {
            this.photo = Collections.unmodifiableList(photo);
        }
    }
}
