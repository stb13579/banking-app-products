package com.bankingapp.products.repository;

import com.bankingapp.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByStatus(String status);
    List<Product> findByTypeIgnoreCaseAndStatus(String type, String status);
}
