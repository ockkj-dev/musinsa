package com.example.musinsa.controller;

import com.example.musinsa.model.Product;
import com.example.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testCreateProduct() {
        Product product = new Product();
        product.setCategory("상의");
        product.setBrand("A");
        product.setPrice(100L);

        ResponseEntity<Product> response = restTemplate.postForEntity("/api/product/update", product, Product.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotNull();

        Optional<Product> savedProduct = productRepository.findById(response.getBody().getId());
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getBrand()).isEqualTo("A");
        assertThat(savedProduct.get().getPrice()).isEqualTo(100);
        assertThat(savedProduct.get().getCategory()).isEqualTo("상의");
    }

    @Test
    public void testUpdateProduct() {
        Product product = new Product();
        product.setCategory("하의");
        product.setBrand("C");
        product.setPrice(120L);
        product = productRepository.save(product);

        Product updatedProduct = new Product();

        updatedProduct.setId(product.getId());
        updatedProduct.setCategory("하의");
        updatedProduct.setBrand("C");
        updatedProduct.setPrice(130L);

        restTemplate.postForEntity("/api/product/update", updatedProduct, Product.class);

        Optional<Product> savedProduct = productRepository.findById(product.getId());
        assertThat(savedProduct).isPresent();
        assertThat(savedProduct.get().getCategory()).isEqualTo("하의");
        assertThat(savedProduct.get().getBrand()).isEqualTo("C");
        assertThat(savedProduct.get().getPrice()).isEqualTo(130);
    }

    @Test
    public void testDeleteProduct() {
        Product product = new Product();
        product.setCategory("신발");
        product.setBrand("E");
        product.setPrice(400L);
        product = productRepository.save(product);

        restTemplate.delete("/api/product/delete/" + product.getId());

        Optional<Product> deletedProduct = productRepository.findById(product.getId());
        assertThat(deletedProduct).isNotPresent();
    }
}

