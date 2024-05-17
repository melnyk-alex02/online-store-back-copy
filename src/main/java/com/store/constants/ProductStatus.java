package com.store.constants;

import lombok.Getter;

@Getter
public enum ProductStatus {
    IN_STOCK("IN STOCK"),
    OUT_OF_STOCK("OUT OF STOCK");

    private final String productStatus;

    ProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }
}