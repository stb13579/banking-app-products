import { Controller, Get, Post, Body, UseGuards, Request } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtGuard } from '../auth/jwt.guard';
import { ProductsService } from './products.service';
import { ApplyCreditCardDto } from './dto/apply-credit-card.dto';
import { ApplyLoanDto } from './dto/apply-loan.dto';

@ApiTags('Products')
@ApiBearerAuth()
@UseGuards(JwtGuard)
@Controller('products')
export class ProductsController {
  constructor(private readonly productsService: ProductsService) {}

  @Get()
  list() {
    return this.productsService.getProducts();
  }

  @Post('credit-card/apply')
  applyCreditCard(@Request() req, @Body() dto: ApplyCreditCardDto) {
    return this.productsService.applyCreditCard(req.user.sub, dto);
  }

  @Post('loan/apply')
  applyLoan(@Request() req, @Body() dto: ApplyLoanDto) {
    return this.productsService.applyLoan(req.user.sub, dto);
  }
}
