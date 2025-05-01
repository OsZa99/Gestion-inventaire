package com.inventoryapi.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.inventoryapi.models.Reservation;
import com.inventoryapi.models.Stock;
import com.inventoryapi.repositories.ReservationRepository;
import com.inventoryapi.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventoryapi.models.Product;
import com.inventoryapi.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public boolean checkStockAvailability(String storeId, String sku, int quantity) {
        Optional<Stock> stockOpt = stockRepository.findByProduct_SkuAndStoreId(sku, storeId);

        if (!stockOpt.isPresent()) {
            return false;
        }

        Stock stock = stockOpt.get();

        // Récupérer les réservations actives pour calculer le stock disponible
        List<Reservation> activeReservations = reservationRepository
                .findByProductAndStoreIdAndActiveTrue(stock.getProduct(), storeId);

        int reservedQuantity = activeReservations.stream()
                .mapToInt(Reservation::getQuantity)
                .sum();

        // Stock disponible = stock physique - réservations actives
        int availableStock = stock.getQuantity() - reservedQuantity;

        return availableStock >= quantity;
    }

    @Override
    @Transactional
    public boolean updateStock(String storeId, String sku, int quantity) {
        // Trouver le produit par SKU
        Optional<Product> productOpt = productRepository.findBySku(sku);
        if (!productOpt.isPresent()) {
            return false;
        }

        Product product = productOpt.get();

        // Trouver ou créer le stock pour ce produit dans ce magasin
        Optional<Stock> stockOpt = stockRepository.findByProductAndStoreId(product, storeId);
        Stock stock;

        if (stockOpt.isPresent()) {
            stock = stockOpt.get();
            stock.setQuantity(quantity);
        } else {
            stock = new Stock(product, storeId, quantity);
        }

        stockRepository.save(stock);

        // Simuler la synchronisation avec d'autres magasins (pourrait avoir un bug ici)
        // syncStockWithOtherStores(product, storeId);

        return true;
    }

    @Override
    @Transactional
    public String reserveProduct(String storeId, String sku, int quantity) {
        // Vérifier la disponibilité
        if (!checkStockAvailability(storeId, sku, quantity)) {
            return null; // Stock insuffisant
        }

        // Trouver le produit
        Optional<Product> productOpt = productRepository.findBySku(sku);
        if (!productOpt.isPresent()) {
            return null;
        }

        Product product = productOpt.get();

        // Générer un code de réservation unique
        String reservationCode = UUID.randomUUID().toString();

        // Créer la réservation
        Reservation reservation = new Reservation(product, storeId, quantity, reservationCode);
        reservationRepository.save(reservation);

        return reservationCode;
    }

    // Méthode privée qui peut contenir un bug de synchronisation
    private void syncStockWithOtherStores(Product product, String sourceStoreId) {
        // Dans une implémentation réelle, cette méthode synchroniserait
        // les informations de stock entre différents magasins
        // Pour le test, nous pouvons simuler un bug ici
    }
}