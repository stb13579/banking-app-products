import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { JwtModule } from '@nestjs/jwt';
import { ProductsModule } from './products/products.module';
import { ApplicationsModule } from './applications/applications.module';
import { Application } from './applications/entities/application.entity';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: 'postgres',
      url: process.env.DATABASE_URL || 'postgresql://banking:banking@localhost:5432/banking',
      entities: [Application],
      synchronize: true,
      logging: false,
    }),
    JwtModule.register({
      secret: process.env.JWT_SECRET || 'supersecret123',
      signOptions: { expiresIn: '1h' },
      global: true,
    }),
    ProductsModule,
    ApplicationsModule,
  ],
})
export class AppModule {}
