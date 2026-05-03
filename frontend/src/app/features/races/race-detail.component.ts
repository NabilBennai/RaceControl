import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {MeResponse} from '../../core/models/auth.models';
import {RaceRequest, RaceResponse, RaceStatus} from '../../core/models/race.models';
import {RegistrationResponse, RegistrationStatus} from '../../core/models/registration.models';
import {TeamResponse} from '../../core/models/team.models';
import {AuthService} from '../../core/services/auth.service';
import {RaceService} from '../../core/services/race.service';
import {RegistrationService} from '../../core/services/registration.service';
import {TeamService} from '../../core/services/team.service';
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
  registrationForm: FormGroup;
  registrations: RegistrationResponse[] = [];
  teams: TeamResponse[] = [];
  me?: MeResponse;
  readonly statuses: RaceStatus[] = ['SCHEDULED', 'REGISTRATION_OPEN', 'LIVE', 'FINISHED', 'CANCELLED'];
  readonly registrationStatuses: RegistrationStatus[] = ['CONFIRMED', 'WAITLISTED', 'CANCELLED', 'REJECTED'];

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly raceService: RaceService,
    private readonly registrationService: RegistrationService,
    private readonly teamService: TeamService,
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
    this.registrationForm = this.fb.nonNullable.group({
      car: ['', [Validators.required, Validators.maxLength(120)]],
      number: ['', [Validators.required, Validators.maxLength(16)]],
      teamId: ['']
    });
  }

  ngOnInit(): void {
    const raceId = Number(this.route.snapshot.paramMap.get('raceId'));
    this.raceService.getById(raceId).subscribe({
      next: (race) => {
        this.race = race;
        this.form.patchValue(race);
        this.loadRegistrations();
        this.loadTeams();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('race.errors.loadFailed'))
    });
    this.authService.me().subscribe({next: (me) => (this.me = me)});
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

  register(): void {
    if (!this.race || this.registrationForm.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('registration.errors.formInvalid'));
      return;
    }
    const raw = this.registrationForm.getRawValue() as { car: string; number: string; teamId: string };
    this.registrationService.create(this.race.id, {
      car: raw.car,
      number: raw.number,
      teamId: raw.teamId ? Number(raw.teamId) : null
    }).subscribe({
      next: () => {
        this.toastService.success(this.t('registration.createdTitle'), this.t('registration.createdMessage'));
        this.loadRegistrations();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('registration.errors.createFailed'))
    });
  }

  unregister(): void {
    if (!this.race) {
      return;
    }
    this.registrationService.deleteMine(this.race.id).subscribe({
      next: () => {
        this.toastService.success(this.t('registration.deletedTitle'), this.t('registration.deletedMessage'));
        this.loadRegistrations();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('registration.errors.deleteFailed'))
    });
  }

  updateRegistrationStatus(registrationId: number, status: RegistrationStatus): void {
    if (!this.race) {
      return;
    }
    this.registrationService.updateStatus(this.race.id, registrationId, {status}).subscribe({
      next: () => {
        this.toastService.success(this.t('registration.statusUpdatedTitle'), this.t('registration.statusUpdatedMessage'));
        this.loadRegistrations();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('registration.errors.statusFailed'))
    });
  }

  isMine(registration: RegistrationResponse): boolean {
    return !!this.me && registration.email === this.me.email;
  }

  private loadRegistrations(): void {
    if (!this.race) {
      return;
    }
    this.registrationService.list(this.race.id).subscribe({
      next: (rows) => (this.registrations = rows),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('registration.errors.listFailed'))
    });
  }

  private loadTeams(): void {
    if (!this.race) {
      return;
    }
    this.teamService.listBySeason(this.race.seasonId).subscribe({next: (teams) => (this.teams = teams)});
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
