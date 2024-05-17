package com.store.entity;

import com.store.utils.SlugUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 _,.-]+$", message = "Name should only contain alphanumeric characters")
    @Size(min = 2, max = 50, message = "Title should be between 2 and 50 characters long")
    private String title;

    private String slug;

    @NotNull
    @Size(min = 2, max = 50, message = "Author name should be between 2 and 50 characters long")
    private String authorName;

    @NotNull
    private String posterImgSrc;

    private String postStatus;

    @NotNull
    @Size(min = 2, message = "Content should be at least 2 characters long")
    private String content;

    private boolean hero;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime publishedAt;

    @PrePersist
    public void prePersist() {
        this.slug = SlugUtils.generateSlug(this.title);
    }
}