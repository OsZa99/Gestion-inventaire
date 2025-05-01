package com.inventoryapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventoryapi.models.Product;
import com.inventoryapi.models.Reservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByProductAndStoreIdAndActiveTrue(Product product, String storeId);
    List<Reservation> findByExpiresAtBeforeAndActiveTrue(LocalDateTime dateTime);
    Optional<Reservation> findByReservationCode(String reservationCode);
}