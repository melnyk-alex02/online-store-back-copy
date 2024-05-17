package com.store.dto.categoryDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryCreateDTO {
    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9!@#$%^&*() ]+$", message = "Name should only contain alphanumeric characters")
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters long")
    private String name;
    @Pattern(regexp = "^[A-Za-z0-9 ]+$", message = "Title should only contain alphanumeric characters")
    @Size(min = 2, max = 50, message = "Title should be between 2 and 50 characters long")
    private String title;
    @NotNull
    @NotBlank
    @Size(min = 2, message = "Overview should be between more than 2 long")
    private String description;
    @NotNull
    @NotBlank
    @Size(min = 2, message = "Overview should be between more than 2 long")
    private String overview;
    private String path;
    private String imgSrc;
    private Double coordinateOnBannerX;
    private Double coordinateOnBannerY;
    private String createdAt;
}