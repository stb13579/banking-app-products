import { Controller, Get, Param, UseGuards, Request } from '@nestjs/common';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { JwtGuard } from '../auth/jwt.guard';
import { ApplicationsService } from './applications.service';

@ApiTags('Applications')
@ApiBearerAuth()
@UseGuards(JwtGuard)
@Controller('applications')
export class ApplicationsController {
  constructor(private readonly applicationsService: ApplicationsService) {}

  @Get()
  listMine(@Request() req) {
    return this.applicationsService.findByUser(req.user.sub);
  }

  @Get(':id')
  getOne(@Param('id') id: string) {
    /**
     * VULNERABILITY (IDOR): No ownership check is performed.
     * Any authenticated user can fetch any application by guessing the UUID.
     * Fix: verify app.user_id === req.user.sub after fetching.
     * Aikido SAST will flag this missing authorization check.
     */
    return this.applicationsService.findById(id);
  }
}
