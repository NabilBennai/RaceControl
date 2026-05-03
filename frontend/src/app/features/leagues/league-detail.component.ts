import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {LeagueRequest, LeagueResponse} from '../../core/models/league.models';
import {LeagueService} from '../../core/services/league.service';
import {ToastService} from '../../shared/services/toast.service';
import {LeagueFormComponent} from './components/league-form.component';

@Component({
  selector: 'app-league-detail',
  imports: [TranslatePipe, RouterLink, LeagueFormComponent],
  templateUrl: './league-detail.component.html',
  styleUrl: './league-detail.component.scss'
})
export class LeagueDetailComponent implements OnInit {
  league?: LeagueResponse;
  canEdit = false;
  canDelete = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly leagueService: LeagueService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    const leagueId = Number(this.route.snapshot.paramMap.get('id'));
    if (!leagueId) {
      this.router.navigateByUrl('/leagues');
      return;
    }

    this.leagueService.getById(leagueId).subscribe({
      next: (league) => {
        this.league = league;
        this.canEdit = league.myRole === 'OWNER' || league.myRole === 'ADMIN';
        this.canDelete = league.myRole === 'OWNER';
      },
      error: () => {
        this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.loadFailed'));
        this.router.navigateByUrl('/leagues');
      }
    });
  }

  save(payload: LeagueRequest): void {
    if (!this.league) {
      return;
    }

    this.leagueService.update(this.league.id, payload).subscribe({
      next: (league) => {
        this.league = league;
        this.toastService.success(this.t('league.updatedTitle'), this.t('league.updatedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.updateFailed'))
    });
  }

  remove(): void {
    if (!this.league || !this.canDelete) {
      return;
    }

    this.leagueService.delete(this.league.id).subscribe({
      next: () => {
        this.toastService.success(this.t('league.deletedTitle'), this.t('league.deletedMessage'));
        this.router.navigateByUrl('/leagues');
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.deleteFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
