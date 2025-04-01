package com.example.musinsa.contorller;

import com.example.musinsa.model.Product;
import com.example.musinsa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 1. 카테고리별 최저가격
     * @return 각 카테고리별 최소 가격 리스트 및 총액
     */
    @GetMapping("/min-prices")
    public ResponseEntity<Map<String, Object>> getMinimumPrices() {
        return ResponseEntity.ok(productService.getMinimumPrices());
    }

    /**
     * 2. 단일 브랜드 최저가격
     * @return 총액 최저가격 브랜드와 각 카테고리 상품 리스트
     */
    @GetMapping("/cheapest-brand")
    public ResponseEntity<Map<String, Object>> getCheapestBrand() {
        return ResponseEntity.ok(productService.getCheapestBrandForAllCategories());
    }

    /**
     * 3. 카테고리별 최저/최고 가격
     * @param category
     * @return 카테고리별 최저/최고 가격과 브랜드
     */
    @GetMapping("/{category}/price-range")
    public ResponseEntity<Map<String, Object>> getPriceRangeForCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getPriceRangeForCategory(category));
    }

    /**
     * 4. 상품 추가/수정 (id 가 없으면 추가, 있으면 수정)
     * @param product
     * @return 추가 또는 수정한 데이터
     */
    @PostMapping("/update")
    public ResponseEntity<Product> addOrUpdateProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.addOrUpdateProduct(product));
    }

    /**
     * 5. 상품 삭제
     * @param id
     * @return 삭제 메시지
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("삭제되었습니다.");
    }

    /**
     * 6. 전체 상품 검색
     * @return 전체 상품 리스트
     */
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
