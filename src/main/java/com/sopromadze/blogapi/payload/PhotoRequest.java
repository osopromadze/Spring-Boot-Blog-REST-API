package com.sopromadze.blogapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhotoRequest {

    @NotBlank
    @Size(min = 3)
    private String title;

    @NotBlank
    @Size(min = 10)
    private String url;

    @NotBlank
    @Size(min = 10)
    private String thumbnailUrl;

    @NotNull
    private Long albumId;
}
