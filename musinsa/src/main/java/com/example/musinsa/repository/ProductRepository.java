package com.example.musinsa.repository;

import com.example.musinsa.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);

    @Query("""
        SELECT new map(p.category as category, p.price as price, p.brand as brand)
        FROM Product p
        WHERE p.id = (
            SELECT p2.id
            FROM Product p2
            WHERE p2.category = p.category
            ORDER BY p2.price ASC
            LIMIT 1
        )
    """)
    List<Map<String, Object>> findMinPricesByCategory();
}
