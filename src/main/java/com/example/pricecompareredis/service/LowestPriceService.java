package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Product;

import java.util.Set;

public interface LowestPriceService {
    Set getZsetValue(String key);
    int setNewProduct(Product newProduct);
}
