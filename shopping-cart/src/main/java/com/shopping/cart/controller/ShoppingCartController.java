package com.shopping.cart.controller;

import com.shopping.cart.dto.Receipt;
import com.shopping.cart.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/cart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

    @PostMapping("/calculate-total")
    public ResponseEntity<Receipt> calculateTotal(
            @RequestParam(name = "cart") MultipartFile multipartFile) {
        try {
            Receipt receipt = shoppingCartService.calculateGrandTotal(multipartFile);
            logger.info("Receipt for shopping : {} ", receipt);
            return ResponseEntity.status(HttpStatus.OK).body(receipt);
        } catch (Exception e) {
            logger.info("Error while generating receipt for shopping : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

    }

    @PostMapping("/apply-tax")
    public ResponseEntity<Receipt> applyTax(
            @RequestParam(name = "cart") MultipartFile multipartFile) {
        try {
            Receipt receipt = shoppingCartService.applyTax(multipartFile);
            logger.info("Receipt for shopping : {} ", receipt);
            return ResponseEntity.status(HttpStatus.OK).body(receipt);
        } catch (Exception e) {
            logger.info("Error while generating receipt for shopping : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/calculate-tax")
    public ResponseEntity<Receipt> calculateTax(
            @RequestParam(name = "cart") MultipartFile multipartFile) {
        try {
            Receipt receipt = shoppingCartService.calculateTax(multipartFile);
            logger.info("Receipt for shopping : {} ", receipt);
            return ResponseEntity.status(HttpStatus.OK).body(receipt);
        } catch (Exception e) {
            logger.info("Error while generating receipt for shopping : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/apply-discount")
    public ResponseEntity<Receipt> applyDiscount(
            @RequestParam(name = "cart") MultipartFile cartFile,
            @RequestParam(name = "coupons") MultipartFile couponsFile) {
        try {
            Receipt receipt = shoppingCartService.applyDiscount(cartFile, couponsFile);
            logger.info("Receipt for shopping : {} ", receipt);
            return ResponseEntity.status(HttpStatus.OK).body(receipt);
        } catch (Exception e) {
            logger.info("Error while generating receipt for shopping : {} ", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
