package com.shopping.cart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Receipt {

    private Double subTotal;

    private Double discountTotal;

    private Double subTotalAfterDiscount;

    private Double taxableSubTotal;

    private Double taxTotal;

    private Double grandTotal;

    public Receipt(Double grandTotal) {
        super();
        this.grandTotal = grandTotal;
    }

    public Receipt(Double subTotal, Double taxTotal, Double grandTotal) {
        super();
        this.subTotal = subTotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
    }

    public Receipt(Double subTotal, Double taxableSubTotal, Double taxTotal, Double grandTotal) {
        super();
        this.subTotal = subTotal;
        this.taxableSubTotal = taxableSubTotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
    }
}
