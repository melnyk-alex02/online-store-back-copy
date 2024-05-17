package com.store.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String title;
    private String path;
    private String description;
    private String overview;
    private String imgSrc;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private Double coordinateOnBannerX;
    private Double coordinateOnBannerY;
    @Transient
    private long productCount;
}