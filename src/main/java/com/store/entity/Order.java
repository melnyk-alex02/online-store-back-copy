package com.store.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private String email;
    private String userId;
    private String orderStatus;
    private ZonedDateTime createdDate;
    private ZonedDateTime statusLastUpdatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProductList;
    private String commentOfManager;
    @ManyToOne
    private ShippingMethod shippingMethod;
    private BigDecimal priceOfProducts; // without subtractions
    private BigDecimal totalPrice;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    @ManyToOne
    private Discount discount;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
    private boolean agreementToTerms;
    private boolean emailMeWithOffersAndNews;
}