import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {SeasonRequest, SeasonResponse} from '../../core/models/season.models';
import {SeasonService} from '../../core/services/season.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-season-detail',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './season-detail.component.html',
  styleUrl: './season-detail.component.scss'
})
export class SeasonDetailComponent implements OnInit {
  season?: SeasonResponse;
  form: FormGroup;

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
    const seasonId = Number(this.route.snapshot.paramMap.get('seasonId'));
    this.seasonService.getById(seasonId).subscribe({
      next: (season) => {
        this.season = season;
        this.form.patchValue({
          name: season.name,
          startDate: season.startDate,
          endDate: season.endDate
        });
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.loadFailed'))
    });
  }

  update(): void {
    if (!this.season || this.form.invalid) {
      return;
    }
    this.seasonService.update(this.season.id, this.form.getRawValue() as SeasonRequest).subscribe({
      next: (season) => {
        this.season = season;
        this.toastService.success(this.t('season.updatedTitle'), this.t('season.updatedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.updateFailed'))
    });
  }

  activate(): void {
    if (!this.season) {
      return;
    }
    this.seasonService.activate(this.season.id).subscribe({
      next: (season) => {
        this.season = season;
        this.toastService.success(this.t('season.activatedTitle'), this.t('season.activatedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.activateFailed'))
    });
  }

  finish(): void {
    if (!this.season) {
      return;
    }
    this.seasonService.finish(this.season.id).subscribe({
      next: (season) => {
        this.season = season;
        this.toastService.success(this.t('season.finishedTitle'), this.t('season.finishedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.finishFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
