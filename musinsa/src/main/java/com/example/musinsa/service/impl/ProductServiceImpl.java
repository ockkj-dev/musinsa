package com.example.musinsa.service.impl;

import com.example.musinsa.repository.ProductRepository;
import com.example.musinsa.model.Product;
import com.example.musinsa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    // 1. 카테고리별 최저가격 브랜드와 가격 정보 조회
    @Override
    public Map<String, Object> getMinimumPrices() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 카테고리별 최저 가격 조회
            List<Map<String, Object>> minPricesByCategory = productRepository.findMinPricesByCategory();

            // 카테고리별 데이터를 포맷팅
            List<Map<String, Object>> formattedCategoryData = minPricesByCategory.stream()
                    .map(categoryData -> {
                        Map<String, Object> formattedData = new HashMap<>();
                        formattedData.put("카테고리", categoryData.get("category")); // 카테고리 추가
                        formattedData.put("브랜드", categoryData.get("brand"));     // 브랜드 추가
                        formattedData.put("가격", String.format("%,d", ((long) categoryData.get("price")))); // 포맷된 가격 추가
                        return formattedData;
                    })
                    .collect(Collectors.toList());

            // 총액 계산
            long totalPrice = minPricesByCategory.stream()
                    .mapToLong(categoryData -> (long) categoryData.get("price"))
                    .sum();

            // 응답 구성
            response.put("리스트", formattedCategoryData);
            response.put("총액", String.format("%,d", (totalPrice)));

        } catch (Exception e) {
            // 예외 처리: 실패 이유 반환
            response.put("status", "fail");
            response.put("message", e.getMessage());
        }

        return response;
    }
    // 2. 단일 브랜드로 모든 카테고리 상품 구매시 최저 가격과 각 카테고리별 상품 가격 조회
    @Override
    public Map<String, Object> getCheapestBrandForAllCategories() {
        List<Product> allProducts = productRepository.findAll();
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            // 브랜드별로 카테고리 그룹화
            Map<String, List<Product>> brandsByName = allProducts.stream()
                    .collect(Collectors.groupingBy(Product::getBrand));

            String cheapestBrandName = null;
            long cheapestTotalPrice = Long.MAX_VALUE;
            List<Product> cheapestBrandDetails = null;

            // 모든 카테고리를 포함하는 브랜드 찾기
            for (Map.Entry<String, List<Product>> entry : brandsByName.entrySet()) {
                List<Product> brandItems = entry.getValue();

                // 카테고리별 최저가 상품만 선택
                Map<String, Product> categoryMinPriceMap = brandItems.stream()
                        .collect(Collectors.toMap(
                                Product::getCategory,
                                product -> product,
                                (p1, p2) -> p1.getPrice() < p2.getPrice() ? p1 : p2 // 동일 카테고리일 경우 최저가 선택
                        ));

                // 카테고리별 최저가 상품 리스트 추출
                List<Product> minPriceProducts = new ArrayList<>(categoryMinPriceMap.values());

                // 브랜드가 8개의 카테고리를 모두 포함하는지 확인
                Set<String> categoriesCovered = minPriceProducts.stream()
                        .map(Product::getCategory)
                        .collect(Collectors.toSet());
                if (categoriesCovered.size() == 8) { // 카테고리가 8개일 경우
                    long totalPrice = minPriceProducts.stream().mapToLong(Product::getPrice).sum();

                    if (totalPrice < cheapestTotalPrice) { // 최저가 브랜드 판단
                        cheapestBrandName = entry.getKey();
                        cheapestTotalPrice = totalPrice;
                        cheapestBrandDetails = minPriceProducts;
                    }
                }
            }

            // 결과 데이터를 포맷팅하여 응답에 추가

            if (cheapestBrandName != null) {
                Map<String, Object> cheapestBrandInfo = new LinkedHashMap<>();
                cheapestBrandInfo.put("브랜드", cheapestBrandName);

                // 카테고리별 정보 생성
                List<Map<String, String>> categoryInfo = Objects.requireNonNull(cheapestBrandDetails).stream()
                        .map(product -> {
                            Map<String, String> categoryData = new LinkedHashMap<>();
                            categoryData.put("카테고리", product.getCategory());
                            categoryData.put("가격", String.format("%,d", (product.getPrice())));
                            return categoryData;
                        })
                        .collect(Collectors.toList());

                cheapestBrandInfo.put("카테고리", categoryInfo);
                cheapestBrandInfo.put("총액", String.format("%,d", (cheapestTotalPrice)));

                // 응답에 최저가 정보 추가
                response.put("최저가", cheapestBrandInfo);
            } else {
                response.put("status", "fail");
                response.put("message", "모든 카테고리를 포함하는 브랜드가 없습니다.");
            }
        } catch (Exception e) {
            // 예외 처리: 실패 이유 반환
            response.put("status", "fail");
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 3. 카테고리별 최저/최고 가격 조회
    @Override
    public Map<String, Object> getPriceRangeForCategory(String category) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            // 특정 카테고리 내 상품 리스트를 조회
            List<Product> products = productRepository.findByCategory(category);

            if (products == null || products.isEmpty()) {
                // 카테고리에 해당하는 데이터가 없는 경우
                response.put("message", "해당 카테고리의 결과를 찾을 수 없습니다.");
                return response;
            }

            // 최저가 계산
            long minPrice = products.stream()
                    .mapToLong(Product::getPrice)
                    .min()
                    .orElseThrow(() -> new RuntimeException("최저가 계산 실패"));

            // 최고가 계산
            long maxPrice = products.stream()
                    .mapToLong(Product::getPrice)
                    .max()
                    .orElseThrow(() -> new RuntimeException("최고가 계산 실패"));

            // 최저가 상품
            List<Map<String, Object>> minPriceProducts = products.stream()
                    .filter(product -> product.getPrice() == minPrice)
                    .map(product -> {
                        Map<String, Object> productMap = new LinkedHashMap<>();
                        productMap.put("브랜드", product.getBrand());
                        productMap.put("가격", String.format("%,d", product.getPrice()));
                        return productMap;
                    })
                    .collect(Collectors.toList());

            // 최고가 상품들 리스트 생성
            List<Map<String, Object>> maxPriceProducts = products.stream()
                    .filter(product -> product.getPrice() == maxPrice)
                    .map(product -> {
                        Map<String, Object> productMap = new LinkedHashMap<>();
                        productMap.put("브랜드", product.getBrand());
                        productMap.put("가격", String.format("%,d", product.getPrice()));
                        return productMap;
                    })
                    .collect(Collectors.toList());

            response.put("카테고리", category);
            response.put("최저가", minPriceProducts);
            response.put("최고가", maxPriceProducts);
        } catch (Exception e) {
            // 예외 처리
            response.put("status", "fail");
            response.put("message", e.getMessage());
        }

        return response;
    }

    // 4. 새로운 브랜드 및 상품 추가/수정/삭제
    @Override
    public Product addOrUpdateProduct(Product product) {
        return productRepository.save(product);
    }
    @Override
    public Map<String, Object> deleteProduct(Long id) {
        Map<String, Object> response = new LinkedHashMap<>();
        if (id == null) {
            response.put("status", "fail");
            response.put("message", "해당 ID 가 null 입니다.");
        } else if (productRepository.findById(id).isEmpty()) {
            response.put("status", "fail");
            response.put("message", "해당 ID 가 존재하지 않습니다.");
        } else {
            productRepository.deleteById(id);
            response.put("status", "success");
            response.put("message", "정상적으로 삭제되었습니다.");
        }

        return response;
    }

    // 5. 전체 상품 조회 (추가/수정/삭제 확인용)
    @Override
    public List<Map<String, Object>> getAllProducts() {
        List<Product> allProducts = productRepository.findAll();

        List<Map<String, Object>> formattedProductData = allProducts.stream()
                .map(data -> {
                    Map<String, Object> formattedData = new LinkedHashMap<>();
                    formattedData.put("ID", data.getId());
                    formattedData.put("브랜드", data.getBrand());
                    formattedData.put("카테고라", data.getCategory());
                    formattedData.put("가격", String.format("%,d", (data.getPrice())));
                    return formattedData;
                })
                .collect(Collectors.toList());
        return formattedProductData;
    }
}
