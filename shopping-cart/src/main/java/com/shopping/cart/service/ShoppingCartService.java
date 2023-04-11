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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {

    private static Logger logger = LoggerFactory.getLogger(ShoppingCartService.class);

    private static final BigDecimal TAX_RATE = BigDecimal.valueOf(0.0825);

    private Cart mapToCart(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Cart cart = mapper.readValue(file, Cart.class);
        return cart;
    }

    private BigDecimal calculateSum(List<Item> items) {
        BigDecimal sum = items.stream().map(e -> e.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum;
    }

    private BigDecimal calculateTaxableSubTotal(List<Item> items) {
        BigDecimal taxableSubTotal = items.stream().filter(e -> Boolean.TRUE.equals(e.getIsTaxable())).map(e -> e.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return taxableSubTotal;
    }

    public Receipt calculateGrandTotal(MultipartFile multipartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(multipartFile));
        BigDecimal grandTotal = calculateSum(cart.getItems());
        return new Receipt(grandTotal);
    }

    public Receipt applyTax(MultipartFile multipartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(multipartFile));
        BigDecimal subTotal = calculateSum(cart.getItems());
        BigDecimal taxTotal = subTotal.multiply(TAX_RATE);
        BigDecimal grandTotal = subTotal.add(taxTotal);
        return new Receipt(subTotal, taxTotal, grandTotal);
    }

    public Receipt calculateTax(MultipartFile multipartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(multipartFile));
        BigDecimal subTotal = calculateSum(cart.getItems());
        BigDecimal taxableSubTotal = calculateTaxableSubTotal(cart.getItems());
        BigDecimal taxTotal = taxableSubTotal.multiply(TAX_RATE);
        BigDecimal grandTotal = subTotal.add(taxTotal);
        return new Receipt(subTotal, taxableSubTotal, taxTotal, grandTotal);
    }

    public Receipt applyDiscount(MultipartFile cartFile) throws Exception {
        Cart cart = mapToCart(FileUtil.convert(cartFile));
        File couponFile = new ClassPathResource("coupons.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        CouponRequestWrapper couponRequest = mapper.readValue(couponFile, CouponRequestWrapper.class);
        Map<Long, BigDecimal> couponDetails = couponRequest.getCoupons().stream().collect(Collectors.toMap(t -> t.getAppliedSku(), t -> t.getDiscountPrice()));
        BigDecimal subTotalBeforeDiscounts = calculateSum(cart.getItems());

        List<Item> itemsAfterDiscountApplied = new ArrayList<>();
        BigDecimal discountTotal = BigDecimal.ZERO;
        for(Item item : cart.getItems()) {
            Item itemAfterDiscount = new Item(item);
            if (couponDetails.containsKey(item.getSku())) {
                itemAfterDiscount.setPrice(item.getPrice().subtract(couponDetails.get(item.getSku())));
                discountTotal = discountTotal.add(couponDetails.get(item.getSku()));
            }
            itemsAfterDiscountApplied.add(itemAfterDiscount);
        }

        BigDecimal subTotalAfterDiscounts = subTotalBeforeDiscounts.subtract(discountTotal);
        BigDecimal taxableSubTotal = calculateTaxableSubTotal(itemsAfterDiscountApplied);
        BigDecimal taxTotal = taxableSubTotal.multiply(TAX_RATE);
        BigDecimal grandTotal = subTotalAfterDiscounts.add(taxTotal);
        return new Receipt(subTotalBeforeDiscounts, discountTotal, subTotalAfterDiscounts, taxableSubTotal, taxTotal, grandTotal);
    }
}
