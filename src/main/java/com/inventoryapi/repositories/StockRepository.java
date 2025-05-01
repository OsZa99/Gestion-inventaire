package com.inventoryapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventoryapi.models.Product;
import com.inventoryapi.models.Stock;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductAndStoreId(Product product, String storeId);
    Optional<Stock> findByProduct_SkuAndStoreId(String sku, String storeId);
}
