import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {SeasonRequest, SeasonResponse} from '../../core/models/season.models';
import {SeasonService} from '../../core/services/season.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-leagues-seasons',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './leagues-seasons.component.html',
  styleUrl: './leagues-seasons.component.scss'
})
export class LeaguesSeasonsComponent implements OnInit {
  seasons: SeasonResponse[] = [];
  form: FormGroup;
  leagueId = 0;

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly seasonService: SeasonService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.leagueId = Number(this.route.snapshot.paramMap.get('leagueId'));
    this.loadSeasons();
  }

  createSeason(): void {
    if (this.form.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('season.errors.formInvalid'));
      return;
    }

    this.seasonService.create(this.leagueId, this.form.getRawValue() as SeasonRequest).subscribe({
      next: () => {
        this.toastService.success(this.t('season.createdTitle'), this.t('season.createdMessage'));
        this.form.reset();
        this.loadSeasons();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.createFailed'))
    });
  }

  private loadSeasons(): void {
    this.seasonService.listByLeague(this.leagueId).subscribe({
      next: (seasons) => (this.seasons = seasons),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.listFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
