package com.bankingapp.products.service;

import com.bankingapp.products.dto.AmortizationEntry;
import com.bankingapp.products.dto.ApplyCreditCardRequest;
import com.bankingapp.products.dto.ApplyLoanRequest;
import com.bankingapp.products.dto.LoanApplicationResponse;
import com.bankingapp.products.entity.Application;
import com.bankingapp.products.entity.Product;
import com.bankingapp.products.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductsService {

    @Autowired private ProductRepository productRepository;
    @Autowired private ApplicationsService applicationsService;
    @Autowired private CreditBureauService creditBureauService;
    @Autowired private ObjectMapper objectMapper;

    public List<Product> getProducts(String type) {
        if (type != null && !type.isBlank()) {
            return productRepository.findByTypeIgnoreCaseAndStatus(type, "ACTIVE");
        }
        return productRepository.findByStatus("ACTIVE");
    }

    public Optional<Product> getProduct(UUID id) {
        return productRepository.findById(id);
    }

    public Application applyCreditCard(String userId, ApplyCreditCardRequest req) {
        int creditScore = creditBureauService.lookupCreditScore(userId);
        boolean approved = creditScore >= 650
                && req.getAnnualIncome().compareTo(new BigDecimal("30000")) >= 0;

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("annual_income", req.getAnnualIncome());
        details.put("employment_status", req.getEmploymentStatus());
        details.put("credit_score", creditScore);
        if (req.getDesiredLimit() != null) {
            details.put("desired_limit", req.getDesiredLimit());
        }

        Application app = new Application();
        app.setUserId(userId);
        app.setProductType("CREDIT_CARD");
        app.setStatus(approved ? "APPROVED" : "DECLINED");
        app.setDetails(toJson(details));

        return applicationsService.create(app);
    }

    public LoanApplicationResponse applyLoan(String userId, ApplyLoanRequest req) {
        int termMonths = req.getTermMonths() != null ? req.getTermMonths() : 36;
        // Default personal loan APR — realistic range for an unsecured loan
        BigDecimal apr = new BigDecimal("9.99");
        BigDecimal monthlyRate = apr.divide(new BigDecimal("1200"), 10, RoundingMode.HALF_UP);

        BigDecimal monthlyPayment = calculateMonthlyPayment(req.getAmount(), monthlyRate, termMonths);
        BigDecimal monthlyIncome = req.getAnnualIncome()
                .divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);

        // DTI check: monthly payment must not exceed 43% of monthly income
        BigDecimal dti = monthlyIncome.compareTo(BigDecimal.ZERO) > 0
                ? monthlyPayment.divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                : new BigDecimal("1");
        boolean approved = dti.compareTo(new BigDecimal("0.43")) <= 0;

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("purpose", req.getPurpose());
        details.put("annual_income", req.getAnnualIncome());
        details.put("term_months", termMonths);
        details.put("apr", apr);
        details.put("monthly_payment", monthlyPayment);
        details.put("dti_ratio", dti);

        Application app = new Application();
        app.setUserId(userId);
        app.setProductType("PERSONAL_LOAN");
        app.setAmount(req.getAmount());
        app.setTermMonths(termMonths);
        app.setStatus(approved ? "APPROVED" : "DECLINED");
        app.setDetails(toJson(details));

        Application saved = applicationsService.create(app);

        List<AmortizationEntry> schedule = approved
                ? buildAmortizationSchedule(req.getAmount(), monthlyRate, termMonths, monthlyPayment)
                : List.of();

        LoanApplicationResponse response = new LoanApplicationResponse();
        response.setApplication(saved);
        response.setAmortizationSchedule(schedule);
        return response;
    }

    // M = P * r(1+r)^n / ((1+r)^n - 1)
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyRate, int n) {
        BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
        BigDecimal factor = onePlusR.pow(n);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(factor);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private List<AmortizationEntry> buildAmortizationSchedule(
            BigDecimal principal, BigDecimal monthlyRate, int n, BigDecimal monthlyPayment) {

        List<AmortizationEntry> schedule = new ArrayList<>(n);
        BigDecimal balance = principal;

        for (int month = 1; month <= n; month++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPaid = monthlyPayment.subtract(interest);
            balance = balance.subtract(principalPaid).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            schedule.add(new AmortizationEntry(month, monthlyPayment, principalPaid, interest, balance));
        }

        return schedule;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{}";
        }
    }
}
