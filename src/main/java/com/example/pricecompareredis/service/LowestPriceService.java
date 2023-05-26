package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;

import java.util.Set;

public interface LowestPriceService {
    Set getZsetValue(String key);
    Set getZsetValueWithStatus(String key) throws Exception;
    Set getZsetValueWithSpecificException(String key) throws Exception;
    int setNewProduct(Product newProduct);
    int setNewProductGrp(ProductGrp productGrp);
    int setNewProductGrpToKeyword(String keyword, String prodGrpId, double score);
    Keyword getLowestPriceProductByKeyword(String keyword);
}
