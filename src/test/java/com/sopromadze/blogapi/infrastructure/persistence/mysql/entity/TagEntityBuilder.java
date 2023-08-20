package com.sopromadze.blogapi.infrastructure.persistence.mysql.entity;

public class TagEntityBuilder {
    public static TagEntity build() {
        return TagEntity.builder()
                .id(1L)
                .name("name test")
                .build();
    }
}