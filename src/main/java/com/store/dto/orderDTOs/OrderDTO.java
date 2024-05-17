package com.store.dto.orderDTOs;

import com.store.dto.discountDTOs.DiscountDTO;
import com.store.dto.shippingMethodDTOs.ShippingMethodDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDTO {
    private Long id;
    private String number;
    private String orderStatus;
    private ZonedDateTime createdDate;
    private ZonedDateTime statusLastUpdatedAt;
    private String email;
    private String userId;
    private List<OrderProductDTO> orderProductDTOList;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private ShippingMethodDTO shippingMethodDTO;
    private BigDecimal priceOfProducts;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private DiscountDTO discountDTO;
    private BigDecimal totalPrice;
    private boolean agreementToTerms;
    private boolean emailMeWithOffersAndNews;
    private String commentOfManager;
}