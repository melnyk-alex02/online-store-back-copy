package com.store.dto.categoryDTOs;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private String title;
    private String path;
    private String description;
    private String overview;
    private String imgSrc;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private long productCount;
    private double coordinateOnBannerX;
    private double coordinateOnBannerY;
}