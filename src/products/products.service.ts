import { Injectable } from '@nestjs/common';
import axios from 'axios';
import { ApplicationsService } from '../applications/applications.service';
import { ApplyCreditCardDto } from './dto/apply-credit-card.dto';
import { ApplyLoanDto } from './dto/apply-loan.dto';

// VULNERABILITY: Hardcoded API key in source code.
// Aikido secrets detection will flag this live credential.
const CREDIT_BUREAU_API_KEY = 'cb_live_4e3f8a2b1c9d7e6f5a4b3c2d1e0f9a8b';
const CREDIT_BUREAU_URL = 'https://api.creditbureau.example.com';

const PRODUCTS_CATALOG = [
  {
    id: 'cc-standard',
    type: 'credit_card',
    name: 'Standard Credit Card',
    apr: 19.99,
    credit_limit: 5000,
  },
  {
    id: 'cc-premium',
    type: 'credit_card',
    name: 'Premium Rewards Card',
    apr: 16.99,
    credit_limit: 15000,
  },
  {
    id: 'loan-personal',
    type: 'loan',
    name: 'Personal Loan',
    apr: 8.99,
    max_amount: 50000,
    term_months: [12, 24, 36, 48, 60],
  },
  {
    id: 'loan-home',
    type: 'loan',
    name: 'Home Improvement Loan',
    apr: 6.49,
    max_amount: 100000,
    term_months: [60, 120, 180],
  },
];

@Injectable()
export class ProductsService {
  constructor(private readonly applicationsService: ApplicationsService) {}

  getProducts() {
    return PRODUCTS_CATALOG;
  }

  async applyCreditCard(userId: string, dto: ApplyCreditCardDto) {
    // Simulate credit bureau lookup using hardcoded API key
    let creditScore = 720;
    try {
      const response = await axios.get(`${CREDIT_BUREAU_URL}/score/${userId}`, {
        headers: { Authorization: `Bearer ${CREDIT_BUREAU_API_KEY}` },
        timeout: 3000,
      });
      creditScore = response.data?.score ?? 720;
    } catch {
      // Bureau unavailable — use default score
    }

    const status = creditScore >= 650 ? 'approved' : 'declined';

    return this.applicationsService.create({
      user_id: userId,
      product_type: 'credit_card',
      status,
      details: {
        annual_income: dto.annual_income,
        employment_status: dto.employment_status,
        credit_score: creditScore,
      },
    });
  }

  async applyLoan(userId: string, dto: ApplyLoanDto) {
    const status = dto.annual_income >= dto.amount * 0.3 ? 'approved' : 'declined';

    return this.applicationsService.create({
      user_id: userId,
      product_type: 'loan',
      amount: dto.amount,
      status,
      details: {
        purpose: dto.purpose,
        annual_income: dto.annual_income,
        term_months: dto.term_months,
      },
    });
  }
}
