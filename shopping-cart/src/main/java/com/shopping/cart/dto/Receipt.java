package com.shopping.cart.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Receipt {

    private BigDecimal subTotal;

    private BigDecimal discountTotal;

    private BigDecimal subTotalAfterDiscount;

    private BigDecimal taxableSubTotal;

    private BigDecimal taxTotal;

    private BigDecimal grandTotal;

    public Receipt(BigDecimal grandTotal) {
        super();
        this.grandTotal = grandTotal;
    }

    public Receipt(BigDecimal subTotal, BigDecimal taxTotal, BigDecimal grandTotal) {
        super();
        this.subTotal = subTotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
    }

    public Receipt(BigDecimal subTotal, BigDecimal taxableSubTotal, BigDecimal taxTotal, BigDecimal grandTotal) {
        super();
        this.subTotal = subTotal;
        this.taxableSubTotal = taxableSubTotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
    }
}
