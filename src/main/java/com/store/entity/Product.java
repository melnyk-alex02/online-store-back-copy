package com.store.entity;

import com.store.constants.ProductType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String article;

    private String name;

    private BigDecimal price;

    private boolean onSale;

    private BigDecimal salePrice;

    private String color;

    @NotNull
    @ManyToOne
    private Category category;

    @Enumerated(EnumType.STRING)
    private ProductType productType;
    private String description;

    @Type(value = JsonBinaryType.class)
    @Column(name = "productSizes", columnDefinition = "jsonb")
    private List<ProductSize> productSizes;

    @PositiveOrZero
    private int totalQuantity;

    @PositiveOrZero
    private Long unitsSold;

    @NotNull
    private String productStatus;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    @NotNull
    private String imagePath;
}