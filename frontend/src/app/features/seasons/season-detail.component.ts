import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {GamePlatform, SeasonRequest, SeasonResponse} from '../../core/models/season.models';
import {PointRuleType, PointSystemResponse} from '../../core/models/point-system.models';
import {PointSystemService} from '../../core/services/point-system.service';
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
  pointSystemForm: FormGroup;
  pointSystem?: PointSystemResponse;
  readonly ruleTypes: PointRuleType[] = ['FINISH_POSITION', 'POLE_POSITION', 'FASTEST_LAP', 'CLEAN_RACE', 'PARTICIPATION'];

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly seasonService: SeasonService,
    private readonly pointSystemService: PointSystemService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required]
    });
    this.pointSystemForm = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
      rules: this.fb.array([])
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
        this.loadPointSystem();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('season.errors.loadFailed'))
    });
  }

  get rules(): FormArray {
    return this.pointSystemForm.get('rules') as FormArray;
  }

  addRule(type: PointRuleType = 'FINISH_POSITION', positionRank: number | null = 1, points = 25): void {
    this.rules.push(this.fb.nonNullable.group({
      type: [type, Validators.required],
      positionRank: [positionRank],
      points: [points, Validators.required]
    }));
  }

  removeRule(index: number): void {
    this.rules.removeAt(index);
  }

  applyPreset(): void {
    if (!this.season) {
      return;
    }
    const preset = this.getPreset(this.season.gamePlatform);
    this.pointSystemForm.patchValue({name: preset.name});
    this.rules.clear();
    preset.rules.forEach((rule) => this.addRule(rule.type, rule.positionRank, rule.points));
    this.toastService.info(this.t('pointsystem.presetAppliedTitle'), this.t('pointsystem.presetAppliedMessage'));
  }

  savePointSystem(): void {
    if (!this.season || this.pointSystemForm.invalid) {
      return;
    }

    const payload = this.pointSystemForm.getRawValue();
    const request = {
      name: payload.name,
      rules: payload.rules.map((rule: { type: PointRuleType; positionRank: number | null; points: number; }) => ({
        type: rule.type,
        positionRank: rule.type === 'FINISH_POSITION' ? rule.positionRank : null,
        points: Number(rule.points)
      }))
    };

    const obs = this.pointSystem
      ? this.pointSystemService.update(this.pointSystem.id, request)
      : this.pointSystemService.create(this.season.id, request);

    obs.subscribe({
      next: (pointSystem) => {
        this.pointSystem = pointSystem;
        this.patchPointSystem(pointSystem);
        this.toastService.success(this.t('pointsystem.savedTitle'), this.t('pointsystem.savedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('pointsystem.errors.saveFailed'))
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

  private loadPointSystem(): void {
    if (!this.season) {
      return;
    }
    this.pointSystemService.getBySeason(this.season.id).subscribe({
      next: (pointSystem) => {
        this.pointSystem = pointSystem;
        this.patchPointSystem(pointSystem);
      },
      error: () => {
        this.pointSystem = undefined;
        this.applyPreset();
      }
    });
  }

  private patchPointSystem(pointSystem: PointSystemResponse): void {
    this.pointSystemForm.patchValue({name: pointSystem.name});
    this.rules.clear();
    pointSystem.rules.forEach(rule => this.addRule(rule.type, rule.positionRank, rule.points));
  }

  private getPreset(gamePlatform: GamePlatform): { name: string; rules: Array<{ type: PointRuleType; positionRank: number | null; points: number }> } {
    const finish = (values: number[]) => values.map((points, index) => ({
      type: 'FINISH_POSITION' as PointRuleType,
      positionRank: index + 1,
      points
    }));

    switch (gamePlatform) {
      case 'F1':
        return {
          name: 'Preset F1 Officiel',
          rules: [
            ...finish([25, 18, 15, 12, 10, 8, 6, 4, 2, 1]),
            {type: 'FASTEST_LAP', positionRank: null, points: 1},
            {type: 'POLE_POSITION', positionRank: null, points: 1}
          ]
        };
      case 'ACC':
        return {
          name: 'Preset ACC Sprint',
          rules: [
            ...finish([20, 15, 12, 10, 8, 6, 4, 3, 2, 1]),
            {type: 'POLE_POSITION', positionRank: null, points: 1},
            {type: 'FASTEST_LAP', positionRank: null, points: 1}
          ]
        };
      case 'IRACING':
        return {
          name: 'Preset iRacing League',
          rules: [
            ...finish([40, 35, 30, 26, 22, 18, 15, 12, 10, 8]),
            {type: 'PARTICIPATION', positionRank: null, points: 2}
          ]
        };
      case 'LMU':
        return {
          name: 'Preset LMU Endurance',
          rules: [
            ...finish([25, 18, 15, 12, 10, 8, 6, 4, 2, 1]),
            {type: 'CLEAN_RACE', positionRank: null, points: 2}
          ]
        };
      case 'RF2':
        return {
          name: 'Preset rFactor 2',
          rules: [
            ...finish([30, 24, 20, 16, 13, 10, 8, 6, 4, 2]),
            {type: 'POLE_POSITION', positionRank: null, points: 1}
          ]
        };
      default:
        return {
          name: 'Barème standard',
          rules: [
            ...finish([25, 18, 15, 12, 10, 8, 6, 4, 2, 1]),
            {type: 'FASTEST_LAP', positionRank: null, points: 1}
          ]
        };
    }
  }
}
