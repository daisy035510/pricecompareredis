package com.example.pricecompareredis.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProductGrp {
    private String prodGrpId;
    private List<Product> productList;
}
