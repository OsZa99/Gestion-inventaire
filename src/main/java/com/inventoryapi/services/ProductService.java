package com.inventoryapi.services;

import java.util.List;
import java.util.Optional;

import com.inventoryapi.models.Product;

public interface ProductService {

    /**
     * Récupérer tous les produits
     */
    List<Product> getAllProducts();

    /**
     * Récupérer un produit par son ID
     */
    Optional<Product> getProductById(Long id);

    /**
     * Enregistrer ou mettre à jour un produit
     */
    Product saveProduct(Product product);

    /**
     * Supprimer un produit
     */
    void deleteProduct(Long id);

    /**
     * Vérifier la disponibilité du stock
     */
    boolean checkStockAvailability(String storeId, String sku, int quantity);

    /**
     * Mettre à jour le stock d'un produit
     */
    boolean updateStock(String storeId, String sku, int quantity);

    /**
     * Réserver un produit
     */
    String reserveProduct(String storeId, String sku, int quantity);
}
