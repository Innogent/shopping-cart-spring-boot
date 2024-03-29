package com.shopping.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    private String couponName;
    private Long appliedSku;
    private BigDecimal discountPrice;
}
