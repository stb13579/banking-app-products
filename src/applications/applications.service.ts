import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Application } from './entities/application.entity';

@Injectable()
export class ApplicationsService {
  constructor(
    @InjectRepository(Application)
    private readonly repo: Repository<Application>,
  ) {}

  async create(data: Partial<Application>): Promise<Application> {
    const app = this.repo.create(data);
    return this.repo.save(app);
  }

  async findByUser(userId: string): Promise<Application[]> {
    return this.repo.find({ where: { user_id: userId } });
  }

  async findById(id: string): Promise<Application> {
    const app = await this.repo.findOne({ where: { id } });
    if (!app) throw new NotFoundException('Application not found');
    return app;
  }
}
