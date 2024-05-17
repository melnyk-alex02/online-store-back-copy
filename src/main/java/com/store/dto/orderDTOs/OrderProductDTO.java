package com.store.dto.orderDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderProductDTO {
    private String orderNumber;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImagePath;
    private String productSize;
    private String productColor;
    private String productArticle;
    private boolean onSale;
    private BigDecimal salePrice;
    private Integer count;
}