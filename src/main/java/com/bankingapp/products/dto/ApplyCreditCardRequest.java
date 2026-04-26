package com.bankingapp.products.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyCreditCardRequest {

    @NotNull(message = "annual_income is required")
    @DecimalMin(value = "0.00", message = "annual_income must be non-negative")
    private BigDecimal annualIncome;

    @NotBlank(message = "employment_status is required")
    private String employmentStatus;

    private String desiredLimit;
}
