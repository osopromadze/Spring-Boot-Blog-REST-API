package com.sopromadze.blogapi.domain.model;

public class TagBuilder {

    public static Tag build() {
        return Tag.builder()
                .tagId("1")
                .name("test name")
                .build();
    }
}