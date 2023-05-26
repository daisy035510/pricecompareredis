package com.example.pricecompareredis.controller;

import com.example.pricecompareredis.service.LowestPriceService;
import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.NotFoundException;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.Set;

/**
 * TO-DO
 * controller는 대문자로 시작하나?
 */
@RestController
@RequestMapping("/")
public class LowestPriceController {

    @Autowired
    private LowestPriceService mlps;

    @GetMapping("/product")
    public Set getZsetValue(String key) {
        return mlps.getZsetValue(key);
    }

    @GetMapping("/product1")
    public Set getZsetValueWithStatus(String key) {
        try {
            return mlps.getZsetValueWithStatus(key);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @GetMapping("/product2")
    public Set GetZsetValueUsingExController (String key) throws Exception {
        try {
            return mlps.getZsetValueWithStatus(key);
        }
        catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    @GetMapping("/product3")
    public ResponseEntity<Set> GetZsetValueUsingExControllerWithSpecificException (String key) throws Exception {
        Set<String> mySet = new HashSet<>();
        try {
            mySet =  mlps.getZsetValueWithSpecificException(key);
        }
        catch (NotFoundException ex) {
            throw new NotFoundException(ex.getErrmsg(), HttpStatus.NOT_FOUND);
        }
        HttpHeaders responseHeaders = new HttpHeaders();

        return new ResponseEntity<Set>(mySet, responseHeaders, HttpStatus.OK);
    }
    @PutMapping("/product")
    public int setNewProduct(@RequestBody Product newProduct) {
        return mlps.setNewProduct(newProduct);
    }

    @PutMapping("/productGroup")
    public int setNewProductGroup(@RequestBody ProductGrp newProductGrp) {
        return mlps.setNewProductGrp(newProductGrp);
    }

    @PutMapping("/productGroupToKeyword")
    public int setNewProductGrpToKeyword(String keyword, String prodGrpId, double score) {
        return mlps.setNewProductGrpToKeyword(keyword, prodGrpId, score);
    }

    @GetMapping("/productPrice/lowest")
    public Keyword getLowestPriceProductByKeyword(String keyword) {
        return mlps.getLowestPriceProductByKeyword(keyword);
    }
}
