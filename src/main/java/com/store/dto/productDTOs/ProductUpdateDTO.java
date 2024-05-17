package com.store.dto.productDTOs;


import com.store.constants.ProductType;
import com.store.entity.ProductSize;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductUpdateDTO {
    private Long id;

    @NotNull
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Article should only contain alphanumeric and uppercase characters")
    @Size(min = 3, max = 8, message = "Article should be between 3 and 8 characters long")
    private String article;
    @NotBlank
    @NotNull
    @Size(min = 2, max = 50, message = "Name should be between 2 and 50 characters long")
    private String name;
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    private boolean onSale;
    private String color;
    private BigDecimal salePrice;
    @NotNull
    private Long categoryId;
    @NotNull
    @NotBlank
    @Size(min = 2, message = "Description should be at least 2 characters long")
    private String description;
    private List<ProductSize> productSizes;
    private ProductType productType;
    @PositiveOrZero
    private int totalQuantity;
    private String productStatus;
    @NotNull
    private String imagePath;
}