import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {DriverStandingResponse, TeamStandingResponse} from '../../core/models/standing.models';
import {StandingService} from '../../core/services/standing.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-season-standings',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './season-standings.component.html',
  styleUrl: './season-standings.component.scss'
})
export class SeasonStandingsComponent implements OnInit {
  seasonId = 0;
  drivers: DriverStandingResponse[] = [];
  teams: TeamStandingResponse[] = [];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly standingService: StandingService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.seasonId = Number(this.route.snapshot.paramMap.get('seasonId'));
    this.load();
  }

  recalculate(): void {
    this.standingService.recalculate(this.seasonId).subscribe({
      next: (res) => {
        this.drivers = res.drivers;
        this.teams = res.teams;
        this.toastService.success(this.t('standing.recalculatedTitle'), this.t('standing.recalculatedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('standing.errors.recalculateFailed'))
    });
  }

  private load(): void {
    this.standingService.drivers(this.seasonId).subscribe({
      next: (rows) => (this.drivers = rows),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('standing.errors.loadDriversFailed'))
    });
    this.standingService.teams(this.seasonId).subscribe({
      next: (rows) => (this.teams = rows),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('standing.errors.loadTeamsFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
