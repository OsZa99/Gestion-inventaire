package com.inventoryapi.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventoryapi.models.Product;
import com.inventoryapi.services.ProductService;
import com.inventoryapi.exceptions.ResourceNotFoundException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * Récupère tous les produits
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Récupère un produit par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id);
        }
    }

    /**
     * Crée un nouveau produit
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * Met à jour un produit existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
        Optional<Product> productData = productService.getProductById(id);

        if (productData.isPresent()) {
            Product product = productData.get();
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setPrice(productDetails.getPrice());
            product.setSku(productDetails.getSku());

            return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id);
        }
    }

    /**
     * Supprime un produit
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);

        if (product.isPresent()) {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResourceNotFoundException("Produit non trouvé avec l'ID: " + id);
        }
    }

    /**
     * Vérifie la disponibilité d'un produit dans un magasin
     */
    @GetMapping("/availability/{storeId}/{sku}/{quantity}")
    public boolean checkProductAvailability(
            @PathVariable String storeId,
            @PathVariable String sku,
            @PathVariable int quantity) {
        return productService.checkStockAvailability(storeId, sku, quantity);
    }

    /**
     * Met à jour le stock d'un produit dans un magasin
     */
    @PutMapping("/stock/{storeId}/{sku}")
    public ResponseEntity<Void> updateProductStock(
            @PathVariable String storeId,
            @PathVariable String sku,
            @RequestParam int quantity) {

        boolean success = productService.updateStock(storeId, sku, quantity);

        if (success) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Réserve un produit dans un magasin
     */
    @PostMapping("/reserve/{storeId}/{sku}/{quantity}")
    public ResponseEntity<String> reserveProduct(
            @PathVariable String storeId,
            @PathVariable String sku,
            @PathVariable int quantity) {

        String reservationId = productService.reserveProduct(storeId, sku, quantity);

        if (reservationId != null) {
            return new ResponseEntity<>(reservationId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Stock insuffisant", HttpStatus.BAD_REQUEST);
        }
    }
}