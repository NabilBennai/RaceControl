import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {RaceRequest, RaceResponse, RaceStatus} from '../../core/models/race.models';
import {RaceService} from '../../core/services/race.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-race-detail',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './race-detail.component.html',
  styleUrl: './race-detail.component.scss'
})
export class RaceDetailComponent implements OnInit {
  race?: RaceResponse;
  form: FormGroup;
  readonly statuses: RaceStatus[] = ['SCHEDULED', 'REGISTRATION_OPEN', 'LIVE', 'FINISHED', 'CANCELLED'];

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly raceService: RaceService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      track: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
      raceDate: ['', Validators.required],
      raceTime: ['', Validators.required],
      format: ['', [Validators.required, Validators.maxLength(120)]],
      laps: [null as number | null],
      durationMinutes: [null as number | null],
      weather: ['', [Validators.required, Validators.maxLength(40)]],
      carCategory: ['', [Validators.required, Validators.maxLength(120)]]
    });
  }

  ngOnInit(): void {
    const raceId = Number(this.route.snapshot.paramMap.get('raceId'));
    this.raceService.getById(raceId).subscribe({
      next: (race) => {
        this.race = race;
        this.form.patchValue(race);
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.loadFailed'))
    });
  }

  update(): void {
    if (!this.race || this.form.invalid) {
      return;
    }
    this.raceService.update(this.race.id, this.form.getRawValue() as RaceRequest).subscribe({
      next: (race) => {
        this.race = race;
        this.toastService.success(this.t('race.updatedTitle'), this.t('race.updatedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.updateFailed'))
    });
  }

  setStatus(status: RaceStatus): void {
    if (!this.race) {
      return;
    }
    this.raceService.updateStatus(this.race.id, {status}).subscribe({
      next: (race) => {
        this.race = race;
        this.toastService.success(this.t('race.statusUpdatedTitle'), this.t('race.statusUpdatedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.statusFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
