import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {RaceResponse} from '../../core/models/race.models';
import {RegistrationResponse} from '../../core/models/registration.models';
import {RaceResultRequest, ResultStatus} from '../../core/models/result.models';
import {RaceService} from '../../core/services/race.service';
import {RegistrationService} from '../../core/services/registration.service';
import {ResultService} from '../../core/services/result.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-race-results-edit',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './race-results-edit.component.html',
  styleUrl: './race-results-edit.component.scss'
})
export class RaceResultsEditComponent implements OnInit {
  race?: RaceResponse;
  form: FormGroup;
  hasExistingResult = false;
  readonly statuses: ResultStatus[] = ['CLASSIFIED', 'DNF', 'DNS', 'DSQ'];

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly raceService: RaceService,
    private readonly registrationService: RegistrationService,
    private readonly resultService: ResultService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      lines: this.fb.array([])
    });
  }

  ngOnInit(): void {
    const raceId = Number(this.route.snapshot.paramMap.get('raceId'));
    this.raceService.getById(raceId).subscribe({
      next: (race) => {
        this.race = race;
        this.loadRegistrations(race.id);
        this.loadExistingResult(race.id);
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('result.errors.loadRaceFailed'))
    });
  }

  get lines(): FormArray {
    return this.form.get('lines') as FormArray;
  }

  save(): void {
    if (!this.race || this.form.invalid) {
      return;
    }
    const payload = this.form.getRawValue() as RaceResultRequest;
    const request = {
      lines: payload.lines.map((line) => ({
        ...line,
        totalTime: line.totalTime || null,
        bestLap: line.bestLap || null
      }))
    };

    const obs = this.hasExistingResult
      ? this.resultService.update(this.race.id, request)
      : this.resultService.create(this.race.id, request);

    obs.subscribe({
      next: () => {
        this.hasExistingResult = true;
        this.toastService.success(this.t('result.savedTitle'), this.t('result.savedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('result.errors.saveFailed'))
    });
  }

  deleteResult(): void {
    if (!this.race) {
      return;
    }
    this.resultService.delete(this.race.id).subscribe({
      next: () => {
        this.hasExistingResult = false;
        this.toastService.success(this.t('result.deletedTitle'), this.t('result.deletedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('result.errors.deleteFailed'))
    });
  }

  private loadRegistrations(raceId: number): void {
    this.registrationService.list(raceId).subscribe({
      next: (rows) => this.seedFromRegistrations(rows),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('result.errors.loadRegistrationsFailed'))
    });
  }

  private seedFromRegistrations(registrations: RegistrationResponse[]): void {
    this.lines.clear();
    registrations.forEach((r, index) => {
      this.lines.push(this.fb.nonNullable.group({
        userId: [r.userId, Validators.required],
        username: [r.username],
        position: [index + 1, [Validators.required, Validators.min(1)]],
        totalTime: [''],
        bestLap: [''],
        polePosition: [false],
        incidents: [0, [Validators.required, Validators.min(0)]],
        status: ['CLASSIFIED' as ResultStatus, Validators.required]
      }));
    });
  }

  private loadExistingResult(raceId: number): void {
    this.resultService.get(raceId).subscribe({
      next: (result) => {
        this.hasExistingResult = true;
        this.lines.clear();
        result.lines.forEach((line) => {
          this.lines.push(this.fb.nonNullable.group({
            userId: [line.userId, Validators.required],
            username: [line.username],
            position: [line.position, [Validators.required, Validators.min(1)]],
            totalTime: [line.totalTime ?? ''],
            bestLap: [line.bestLap ?? ''],
            polePosition: [line.polePosition],
            incidents: [line.incidents, [Validators.required, Validators.min(0)]],
            status: [line.status, Validators.required]
          }));
        });
      },
      error: () => {
        this.hasExistingResult = false;
      }
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
