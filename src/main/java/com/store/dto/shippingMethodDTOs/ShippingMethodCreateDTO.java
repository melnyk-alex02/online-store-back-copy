package com.store.dto.shippingMethodDTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ShippingMethodCreateDTO {
    private String name;
    private String description;
    private BigDecimal price;
}