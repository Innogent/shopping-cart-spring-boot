package com.shopping.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    private String itemName;
    private Long sku;
    private Boolean isTaxable;
    private Boolean ownBrand;
    private Double price;
}
