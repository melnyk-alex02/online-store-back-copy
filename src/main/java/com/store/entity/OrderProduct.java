package com.store.entity;

import com.store.entity.compositeId.OrderProductId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrderProduct {
    @EmbeddedId
    private OrderProductId orderProductId;

    @MapsId("orderId")
    @ManyToOne
    private Order order;

    @MapsId("productId")
    @ManyToOne
    private Product product;

    private Integer count;

    private String productSize;
}