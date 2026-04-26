package com.bankingapp.products;

import com.bankingapp.products.entity.Product;
import com.bankingapp.products.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (productRepository.count() > 0) return;

        List<Product> catalog = List.of(
            product("Standard Visa", "CREDIT_CARD",
                "Everyday Visa card with no annual fee and 1% cash back on all purchases.",
                new BigDecimal("21.99"), null, new BigDecimal("5000"),
                new BigDecimal("5000"), "12,24", 30000, 620),

            product("Platinum Rewards", "CREDIT_CARD",
                "Premium rewards card with 2x points on groceries and 1.5x on everything else.",
                new BigDecimal("17.49"), null, new BigDecimal("20000"),
                new BigDecimal("20000"), "12,24", 60000, 700),

            product("Personal Loan", "PERSONAL_LOAN",
                "Unsecured personal loan for any purpose. Fixed rate, no prepayment penalty.",
                new BigDecimal("9.99"), new BigDecimal("1000"), new BigDecimal("50000"),
                null, "12,24,36,48,60", 25000, 640),

            product("Home Improvement Loan", "HOME_LOAN",
                "Low-rate loan for home renovation and improvement projects.",
                new BigDecimal("7.25"), new BigDecimal("5000"), new BigDecimal("100000"),
                null, "60,120,180", 40000, 660),

            product("12-Month CD", "CD",
                "12-month certificate of deposit at 5.10% APY. FDIC insured up to $250,000.",
                new BigDecimal("5.10"), new BigDecimal("500"), new BigDecimal("250000"),
                null, "12", 0, 0)
        );

        productRepository.saveAll(catalog);
        log.info("[products] Seeded {} products", catalog.size());
    }

    private Product product(String name, String type, String description,
                            BigDecimal apr, BigDecimal minAmount, BigDecimal maxAmount,
                            BigDecimal creditLimit, String termMonths,
                            int minIncome, int minCreditScore) {
        Product p = new Product();
        p.setName(name);
        p.setType(type);
        p.setDescription(description);
        p.setApr(apr);
        p.setMinAmount(minAmount);
        p.setMaxAmount(maxAmount);
        p.setCreditLimit(creditLimit);
        p.setTermMonths(termMonths);
        p.setMinIncomeRequired(minIncome);
        p.setMinCreditScore(minCreditScore);
        p.setStatus("ACTIVE");
        return p;
    }
}
