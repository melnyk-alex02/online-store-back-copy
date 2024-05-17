package com.store.dto.postDTOs;

import com.store.constants.PostStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class PostDTO {
    private Long id;
    private String title;
    private String slug;
    private String authorName;
    private String posterImgSrc;
    private PostStatus postStatus;
    private String content;
    private boolean hero;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private ZonedDateTime publishedAt;
}