package com.store.constants;

import lombok.Getter;

@Getter
public enum OrderStatus {
    NEW("NEW"),
    IN_PROGRESS("IN PROGRESS"),
    PROCESSING("PROCESSING"),
    CANCELLED("CANCELLED"),
    SHIPPED("SHIPPED"),
    RETURN_PROCESSING("RETURN PROCESSING"),
    COMPLETED("COMPLETED");

    private final String orderStatus;

    OrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}