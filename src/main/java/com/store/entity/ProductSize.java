package com.store.entity;

import com.store.constants.ProductStatus;
import com.store.constants.SizeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductSize {
    @Enumerated(EnumType.STRING)
    private SizeEnum size;
    private int quantity;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;
}