package com.example.musinsa.service;

import com.example.musinsa.model.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String, Object> getMinimumPrices();

    Map<String, Object> getCheapestBrandForAllCategories();

    Map<String, Object> getPriceRangeForCategory(String category);

    Product addOrUpdateProduct(Product product);

    Map<String, Object> deleteProduct(Long id);

    List<Map<String, Object>> getAllProducts();
}
