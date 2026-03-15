import { IsNumber, IsString, IsOptional, Min } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ApplyLoanDto {
  @ApiProperty({ example: 10000 })
  @IsNumber()
  @Min(1)
  amount: number;

  @ApiProperty({ example: 'home improvement' })
  @IsString()
  purpose: string;

  @ApiProperty({ example: 75000 })
  @IsNumber()
  @Min(0)
  annual_income: number;

  @ApiProperty({ required: false })
  @IsOptional()
  @IsNumber()
  term_months?: number;
}
