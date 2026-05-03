import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {LeagueService} from '../../core/services/league.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-league-join',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './league-join.component.html',
  styleUrl: './league-join.component.scss'
})
export class LeagueJoinComponent {
  readonly byCodeForm: FormGroup;
  readonly leagueId: number;

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly leagueService: LeagueService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.byCodeForm = this.fb.nonNullable.group({
      invitationCode: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(32)]]
    });
    this.leagueId = Number(this.route.snapshot.paramMap.get('id'));
  }

  requestPublicJoin(): void {
    this.leagueService.join(this.leagueId).subscribe({
      next: (response) => this.toastService.success(this.t('league.join.requestTitle'), response.message),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.joinFailed'))
    });
  }

  requestJoinByCode(): void {
    if (this.byCodeForm.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('league.join.codeInvalidFormat'));
      return;
    }

    this.leagueService.joinByCode(this.byCodeForm.getRawValue()).subscribe({
      next: (response) => this.toastService.success(this.t('league.join.codeTitle'), response.message),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.joinByCodeFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
