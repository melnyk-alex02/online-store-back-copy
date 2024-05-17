package com.store.dto.cartProductDTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartProductCreateDTO {
    private Long productId;
    private String size;
    private String color;
    private Integer count;
}