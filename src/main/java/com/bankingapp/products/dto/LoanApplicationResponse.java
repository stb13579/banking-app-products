package com.bankingapp.products.dto;

import com.bankingapp.products.entity.Application;
import lombok.Data;

import java.util.List;

@Data
public class LoanApplicationResponse {
    private Application application;
    private List<AmortizationEntry> amortizationSchedule;
}
