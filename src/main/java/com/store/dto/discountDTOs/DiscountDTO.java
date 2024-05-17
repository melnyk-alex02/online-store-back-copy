package com.store.dto.discountDTOs;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
public class DiscountDTO {
    private Long id;
    private String code;
    private Integer discount;
    private BigDecimal minPrice;
    private ZonedDateTime beginningDate;
    private ZonedDateTime expirationDate;
}