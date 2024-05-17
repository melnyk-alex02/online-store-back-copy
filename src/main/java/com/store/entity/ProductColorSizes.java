package com.store.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ProductColorSizes {
    private String color;
    private List<ProductSize> productSizes;
}