import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {RaceResponse} from '../../core/models/race.models';
import {RaceResultResponse} from '../../core/models/result.models';
import {RaceService} from '../../core/services/race.service';
import {ResultService} from '../../core/services/result.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-race-results-view',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './race-results-view.component.html',
  styleUrl: './race-results-view.component.scss'
})
export class RaceResultsViewComponent implements OnInit {
  race?: RaceResponse;
  result?: RaceResultResponse;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly raceService: RaceService,
    private readonly resultService: ResultService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    const raceId = Number(this.route.snapshot.paramMap.get('raceId'));
    this.raceService.getById(raceId).subscribe({
      next: (race) => (this.race = race),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('result.errors.loadRaceFailed'))
    });
    this.resultService.get(raceId).subscribe({
      next: (res) => (this.result = res),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('result.errors.loadFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
