import { IsNumber, IsString, IsOptional, Min } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class ApplyCreditCardDto {
  @ApiProperty({ example: 75000 })
  @IsNumber()
  @Min(0)
  annual_income: number;

  @ApiProperty({ example: 'employed' })
  @IsString()
  employment_status: string;

  @ApiProperty({ required: false })
  @IsOptional()
  @IsString()
  desired_limit?: string;
}
