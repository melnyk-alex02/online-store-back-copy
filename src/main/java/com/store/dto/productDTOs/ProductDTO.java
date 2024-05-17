package com.store.dto.productDTOs;


import com.store.constants.ProductType;
import com.store.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String article;
    private String name;
    private BigDecimal price;
    private String color;
    private boolean onSale;
    private BigDecimal salePrice;
    private Long categoryId;
    private String categoryName;
    private String description;
    private ProductType productType;
    private List<ProductSize> productSizes;
    private int totalQuantity;
    private Long unitsSold;
    private String productStatus;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private String imagePath;
}