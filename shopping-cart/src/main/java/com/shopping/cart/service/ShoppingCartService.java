package com.shopping.cart.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.cart.dto.Cart;
import com.shopping.cart.dto.CouponRequestWrapper;
import com.shopping.cart.dto.Item;
import com.shopping.cart.dto.Receipt;
import com.shopping.cart.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private static Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    private static final Double TAX_RATE = 0.0825;

    private Cart mapToCart(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Cart cart = mapper.readValue(file, Cart.class);
        return cart;
    }

    private Double calculateSum(List<Item> items) {
        Double sum = items.stream().map(e -> e.getPrice()).reduce(0.0, Double::sum);
        return sum;
    }

    private Double calculateTaxableSubTotal(List<Item> items) {
        Double taxableSubTotal = items.stream().filter(e -> Boolean.TRUE.equals(e.getIsTaxable())).map(e -> e.getPrice()).reduce(0.0, Double::sum);
        return taxableSubTotal;
    }

    public Receipt calculateGrandTotal(MultipartFile multipartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(multipartFile));
        Double grandTotal = calculateSum(cart.getItems());
        return new Receipt(grandTotal);
    }

    public Receipt applyTax(MultipartFile multipartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(multipartFile));
        Double subTotal = calculateSum(cart.getItems());
        Double taxTotal = TAX_RATE * subTotal;
        Double grandTotal = subTotal + taxTotal;
        return new Receipt(subTotal, taxTotal, grandTotal);
    }

    public Receipt calculateTax(MultipartFile multipartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(multipartFile));
        Double subTotal = calculateSum(cart.getItems());
        Double taxableSubTotal = calculateTaxableSubTotal(cart.getItems());
        Double taxTotal = TAX_RATE * taxableSubTotal;
        Double grandTotal = subTotal + taxTotal;
        return new Receipt(subTotal, taxableSubTotal, taxTotal, grandTotal);
    }

    public Receipt applyDiscount(MultipartFile cartFile, MultipartFile couponFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(cartFile));
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        CouponRequestWrapper couponRequest = mapper.readValue(FileUtil.convert(couponFile), CouponRequestWrapper.class);
        Map<Long, Double> couponDetails = couponRequest.getCoupons().stream().collect(Collectors.toMap(t -> t.getAppliedSku(), t -> t.getDiscountPrice()));
        Double subTotalBeforeDiscounts = calculateSum(cart.getItems());

        List<Item> itemsAfterDiscountApplied = new ArrayList<>();
        Double discountTotal = 0.0;
        for(Item item : cart.getItems()) {
            Item itemAfterDiscount = new Item(item);
            if (couponDetails.containsKey(item.getSku())) {
                itemAfterDiscount.setPrice(item.getPrice() - couponDetails.get(item.getSku()));
                discountTotal += couponDetails.get(item.getSku());
            }
            itemsAfterDiscountApplied.add(itemAfterDiscount);
        }

        Double subTotalAfterDiscounts = subTotalBeforeDiscounts - discountTotal;
        Double taxableSubTotal = calculateTaxableSubTotal(itemsAfterDiscountApplied);
        Double taxTotal = TAX_RATE * taxableSubTotal;
        Double grandTotal = subTotalAfterDiscounts + taxTotal;
        return new Receipt(subTotalBeforeDiscounts, discountTotal, subTotalAfterDiscounts, taxableSubTotal, taxTotal, grandTotal);
    }
}
