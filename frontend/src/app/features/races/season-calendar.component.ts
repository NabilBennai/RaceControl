import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {RaceRequest, RaceResponse} from '../../core/models/race.models';
import {SeasonResponse} from '../../core/models/season.models';
import {RaceService} from '../../core/services/race.service';
import {SeasonService} from '../../core/services/season.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-season-calendar',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './season-calendar.component.html',
  styleUrl: './season-calendar.component.scss'
})
export class SeasonCalendarComponent implements OnInit {
  season?: SeasonResponse;
  races: RaceResponse[] = [];
  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly seasonService: SeasonService,
    private readonly raceService: RaceService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      track: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
      raceDate: ['', Validators.required],
      raceTime: ['', Validators.required],
      format: ['Qualification + Course', [Validators.required, Validators.maxLength(120)]],
      laps: [null as number | null],
      durationMinutes: [null as number | null],
      weather: ['DRY', [Validators.required, Validators.maxLength(40)]],
      carCategory: ['', [Validators.required, Validators.maxLength(120)]]
    });
  }

  ngOnInit(): void {
    const seasonId = Number(this.route.snapshot.paramMap.get('seasonId'));
    this.seasonService.getById(seasonId).subscribe({
      next: (season) => {
        this.season = season;
        this.loadRaces();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.loadSeasonFailed'))
    });
  }

  createRace(): void {
    if (!this.season || this.form.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('race.errors.formInvalid'));
      return;
    }
    this.raceService.create(this.season.id, this.form.getRawValue() as RaceRequest).subscribe({
      next: () => {
        this.toastService.success(this.t('race.createdTitle'), this.t('race.createdMessage'));
        this.form.reset({track: '', raceDate: '', raceTime: '', format: 'Qualification + Course', laps: null, durationMinutes: null, weather: 'DRY', carCategory: ''});
        this.loadRaces();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.createFailed'))
    });
  }

  deleteRace(raceId: number): void {
    this.raceService.delete(raceId).subscribe({
      next: () => {
        this.toastService.success(this.t('race.deletedTitle'), this.t('race.deletedMessage'));
        this.loadRaces();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.deleteFailed'))
    });
  }

  private loadRaces(): void {
    if (!this.season) {
      return;
    }
    this.raceService.listBySeason(this.season.id).subscribe({
      next: (races) => (this.races = races),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.listFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
