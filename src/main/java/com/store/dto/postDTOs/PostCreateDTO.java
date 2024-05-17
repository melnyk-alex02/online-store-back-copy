package com.store.dto.postDTOs;

import com.store.constants.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDTO {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9 _,.-]+$", message = "Name should only contain alphanumeric characters")
    @Size(min = 2, max = 50, message = "Title should be between 2 and 50 characters long")
    private String title;
    @NotNull
    @Size(min = 2, max = 50, message = "Author name should be between 2 and 50 characters long")
    private String authorName;
    @NotNull
    private String posterImgSrc;
    @NotNull
    @Size(min = 2, message = "Content should be at least 2 characters long")
    private String content;
    private PostStatus postStatus;
}