package com.example.pricecompareredis.vo;


import lombok.Data;

@Data
public class Product {
    private String prodGrpId; // FPG0001
    private String productId;
    private int price;
}
