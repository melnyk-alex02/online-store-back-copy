package com.store.dto.orderDTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderUpdateDTO {
    private String orderNumber;
    private String billingAddress;
    private String shippingAddress;
    private String shippingMethod;
    private String paymentMethod;
    private String commentOfManager;
}