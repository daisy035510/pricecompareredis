package com.example.pricecompareredis.service;

import com.example.pricecompareredis.vo.Keyword;
import com.example.pricecompareredis.vo.NotFoundException;
import com.example.pricecompareredis.vo.Product;
import com.example.pricecompareredis.vo.ProductGrp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LowestPriceServiceImpl implements LowestPriceService{

    @Autowired
    private RedisTemplate myProdPriceRedis;

    @Override
    public Set getZsetValue(String key) {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
        return myTempSet;
    }


    public Set getZsetValueWithStatus(String key) throws Exception {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
        if (myTempSet.size() < 1 ) {
            throw new Exception("The Key doesn't have any member");
        }
        return myTempSet;
    }

    public Set getZsetValueWithSpecificException(String key) throws Exception {
        Set myTempSet = new HashSet();
        myTempSet = myProdPriceRedis.opsForZSet().rangeWithScores(key, 0, 9);
        if (myTempSet.size() < 1 ) {
            throw new NotFoundException("The Key doesn't exsit in redis", HttpStatus.NOT_FOUND);
        }
        return myTempSet;
    }

    @Override
    public int setNewProduct(Product newProduct) {
        int rank = 0;
        myProdPriceRedis.opsForZSet().add(newProduct.getProdGrpId(), newProduct.getProductId(), newProduct.getPrice());
        rank = myProdPriceRedis.opsForZSet().rank(newProduct.getProdGrpId(), newProduct.getProductId()).intValue();
        return rank;
    }

    @Override
    public int setNewProductGrp(ProductGrp newProductGrp) {

        List<Product> products = newProductGrp.getProductList();
        String productId = products.get(0).getProductId();
        int price = products.get(0).getPrice();
        myProdPriceRedis.opsForZSet().add(newProductGrp.getProdGrpId(), productId, price);
        int productCnt = myProdPriceRedis.opsForZSet().zCard(newProductGrp.getProdGrpId()).intValue();

        return productCnt;
    }

    @Override
    public int setNewProductGrpToKeyword(String keyword, String prodGrpId, double score) {
        myProdPriceRedis.opsForZSet().add(keyword, prodGrpId, score);
        int rank = myProdPriceRedis.opsForZSet().rank(keyword, prodGrpId).intValue();
        return rank;
    }

//    @Override
    public Keyword getLowestPriceProductByKeyword(String keyword) {

        Keyword returnInfo = new Keyword();
        List<ProductGrp> tempProdGrp = new ArrayList<>();
        // Loop 타면서 ProductGroup으로 Product:price 가져오기(10개)
        tempProdGrp = getProdGrpUsingKeyword(keyword);

        // 가져온 정보들을 Rerun할 Object에 넣기
        returnInfo.setKeyword(keyword);
        returnInfo.setProductGrpList(tempProdGrp);

        // 해당 Object rerun
        return returnInfo;
    }

    public List<ProductGrp> getProdGrpUsingKeyword(String keyword) {

        List<ProductGrp> rerunInfo = new ArrayList<>();

        // input 받은 keyword로 productGrpId를 조회
        List<String> prodGrpIdList = new ArrayList<>();
        prodGrpIdList = List.copyOf(myProdPriceRedis.opsForZSet().reverseRange(keyword, 0, 9));
        Product tempProduct = new Product();
        List<Product> tempProdList = new ArrayList<>();

        // 10개 product group id로 loop
        for(final String prodGrpId: prodGrpIdList) {

            ProductGrp tempProdGrp = new ProductGrp();

            Set prodAndPriceList = new HashSet();
            prodAndPriceList = myProdPriceRedis.opsForZSet().rangeWithScores(keyword, 0, 9);
            Iterator<Object> prodPriceObj = prodAndPriceList.iterator();

            while(prodPriceObj.hasNext()) {

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> prodPriceMap = objectMapper.convertValue(prodPriceObj.next(), Map.class);

                tempProduct.setProdGrpId(prodPriceMap.get("value").toString());
                tempProduct.setPrice(Double.valueOf(prodPriceMap.get("score").toString()).intValue());
                tempProdList.add(tempProduct);
            }

            tempProdGrp.setProdGrpId(prodGrpId);
            tempProdGrp.setProductList(tempProdList);
            rerunInfo.add(tempProdGrp);
        }

        return rerunInfo;
    }
}
