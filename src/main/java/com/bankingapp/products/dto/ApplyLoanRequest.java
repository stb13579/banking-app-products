package com.bankingapp.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyLoanRequest {

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1.00", message = "amount must be at least 1.00")
    private BigDecimal amount;

    @NotBlank(message = "purpose is required")
    private String purpose;

    @NotNull(message = "annual_income is required")
    @DecimalMin(value = "0.00", message = "annual_income must be non-negative")
    private BigDecimal annualIncome;

    @Min(value = 1, message = "term_months must be at least 1")
    private Integer termMonths = 36;
}
