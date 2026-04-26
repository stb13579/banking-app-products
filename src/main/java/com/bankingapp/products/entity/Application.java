package com.bankingapp.products.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_applications")
@Getter
@Setter
@NoArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** JWT sub claim — not a foreign key; auth service owns user identity. */
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "product_id")
    private UUID productId;

    /** CREDIT_CARD | PERSONAL_LOAN | HOME_LOAN | CD */
    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "term_months")
    private Integer termMonths;

    /** PENDING | UNDER_REVIEW | APPROVED | DECLINED */
    @Column(nullable = false)
    private String status = "PENDING";

    /** JSON blob: income, credit score, purpose, monthly payment, etc. */
    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
