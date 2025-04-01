package com.example.musinsa.controller;


import com.example.musinsa.contorller.ProductController;
import com.example.musinsa.model.Product;
import com.example.musinsa.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class ProductControllerTest {
    @InjectMocks
    private ProductController productController;

    @Mock
    private ProductService productService;

    public ProductControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnMinimumPrices() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("상의", 10000);
        when(productService.getMinimumPrices()).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = productController.getMinimumPrices();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(productService, times(1)).getMinimumPrices();
    }

    @Test
    void shouldReturnCheapestBrand() {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("brand", "A");
        when(productService.getCheapestBrandForAllCategories()).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = productController.getCheapestBrand();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(productService, times(1)).getCheapestBrandForAllCategories();
    }

    @Test
    void shouldReturnPriceRangeForCategory() {
        String category = "신발";
        Map<String, Object> mockResponse = Map.of("minPrice", 10000, "maxPrice", 50000);
        when(productService.getPriceRangeForCategory(category)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = productController.getPriceRangeForCategory(category);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(productService, times(1)).getPriceRangeForCategory(category);
    }

    @Test
    void shouldAddOrUpdateProduct() {
        Product product = new Product(1L, "A", "신발", 20000L);
        when(productService.addOrUpdateProduct(product)).thenReturn(product);

        ResponseEntity<Product> response = productController.addOrUpdateProduct(product);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(product, response.getBody());
        verify(productService, times(1)).addOrUpdateProduct(product);
    }

    @Test
    void shouldDeleteProduct() {
        Map<String, Object> mockResponse = Map.of("status", "success", "message", "정상적으로 삭제되었습니다.");
        when(productService.deleteProduct(1L)).thenReturn(mockResponse);

        Long id = 1L;

        ResponseEntity<Map<String, Object>> response = productController.deleteProduct(id);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(productService, times(1)).deleteProduct(id);
    }
}