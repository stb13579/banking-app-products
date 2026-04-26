package com.bankingapp.products.controller;

import com.bankingapp.products.dto.ApplyCreditCardRequest;
import com.bankingapp.products.dto.ApplyLoanRequest;
import com.bankingapp.products.dto.LoanApplicationResponse;
import com.bankingapp.products.entity.Application;
import com.bankingapp.products.entity.Product;
import com.bankingapp.products.service.ProductsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@Tag(name = "Products")
public class ProductsController {

    @Autowired
    private ProductsService productsService;

    @GetMapping
    @Operation(summary = "List available products", description = "Returns all active products. Optional ?type= filter (CREDIT_CARD, PERSONAL_LOAN, HOME_LOAN, CD, SAVINGS).")
    public List<Product> list(@RequestParam(required = false) String type) {
        return productsService.getProducts(type);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getOne(@PathVariable UUID id) {
        return productsService.getProduct(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/credit-card/apply")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Apply for a credit card")
    public ResponseEntity<Application> applyCreditCard(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ApplyCreditCardRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productsService.applyCreditCard(userId, req));
    }

    @PostMapping("/loan/apply")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Apply for a personal loan", description = "Returns the application record plus a full amortization schedule if approved.")
    public ResponseEntity<LoanApplicationResponse> applyLoan(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody ApplyLoanRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productsService.applyLoan(userId, req));
    }
}
