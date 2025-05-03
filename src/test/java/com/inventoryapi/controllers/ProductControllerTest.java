package com.inventoryapi.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inventoryapi.models.Product;
import com.inventoryapi.services.ProductService;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Configuration de MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();

        // Initialisation des données de test
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Product for testing");
        testProduct.setPrice(19.99);
        testProduct.setSku("TP-001");
    }

    @Test
    @DisplayName("Test GET /api/products - Récupérer tous les produits")
    void testGetAllProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts())
                .thenReturn(Arrays.asList(testProduct));

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"))
                .andExpect(jsonPath("$[0].sku").value("TP-001"));
    }

    @Test
    @DisplayName("Test GET /api/products/{id} - Récupérer un produit par ID")
    void testGetProductById() throws Exception {
        // Arrange
        when(productService.getProductById(1L))
                .thenReturn(Optional.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    @DisplayName("Test GET /api/products/{id} - Produit non trouvé")
    void testGetProductByIdNotFound() throws Exception {
        // Arrange
        when(productService.getProductById(999L))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test POST /api/products - Créer un nouveau produit")
    void testCreateProduct() throws Exception {
        // Arrange
        when(productService.saveProduct(any(Product.class)))
                .thenReturn(testProduct);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    @DisplayName("Test PUT /api/products/{id} - Mettre à jour un produit")
    void testUpdateProduct() throws Exception {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setPrice(29.99);
        updatedProduct.setSku("TP-001");

        when(productService.getProductById(1L))
                .thenReturn(Optional.of(testProduct));
        when(productService.saveProduct(any(Product.class)))
                .thenReturn(updatedProduct);

        // Act & Assert
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(29.99));
    }

    @Test
    @DisplayName("Test DELETE /api/products/{id} - Supprimer un produit")
    void testDeleteProduct() throws Exception {
        // Arrange
        when(productService.getProductById(1L))
                .thenReturn(Optional.of(testProduct));
        doNothing().when(productService).deleteProduct(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("Test GET /api/products/availability - Vérifier la disponibilité d'un produit")
    void testCheckProductAvailability() throws Exception {
        // Arrange
        when(productService.checkStockAvailability("STORE-001", "TP-001", 5))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/api/products/availability/STORE-001/TP-001/5"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("Test PUT /api/products/stock - Mettre à jour le stock d'un produit")
    void testUpdateProductStock() throws Exception {
        // Arrange
        when(productService.updateStock("STORE-001", "TP-001", 60))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/api/products/stock/STORE-001/TP-001")
                        .param("quantity", "60"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test POST /api/products/reserve - Réserver un produit disponible")
    void testReserveProduct() throws Exception {
        // Arrange
        String reservationCode = "RES-20250215-002";
        when(productService.reserveProduct("STORE-001", "TP-001", 3))
                .thenReturn(reservationCode);

        // Act & Assert
        mockMvc.perform(post("/api/products/reserve/STORE-001/TP-001/3"))
                .andExpect(status().isCreated())
                .andExpect(content().string(reservationCode));
    }

    @Test
    @DisplayName("Test POST /api/products/reserve - Réserver un produit non disponible")
    void testReserveProductUnavailable() throws Exception {
        // Arrange
        when(productService.reserveProduct("STORE-001", "TP-001", 100))
                .thenReturn(null); // Stock insuffisant

        // Act & Assert
        mockMvc.perform(post("/api/products/reserve/STORE-001/TP-001/100"))
                .andExpect(status().isBadRequest());
    }
}