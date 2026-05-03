import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {LeagueMemberResponse} from '../../core/models/league.models';
import {LeagueService} from '../../core/services/league.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-league-members',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './league-members.component.html',
  styleUrl: './league-members.component.scss'
})
export class LeagueMembersComponent implements OnInit {
  members: LeagueMemberResponse[] = [];
  leagueId = 0;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly leagueService: LeagueService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.leagueId = Number(this.route.snapshot.paramMap.get('id'));
    this.reload();
  }

  approve(memberId: number): void {
    this.leagueService.approveMember(this.leagueId, memberId).subscribe({
      next: () => {
        this.toastService.success(this.t('league.members.approvedTitle'), this.t('league.members.approvedMessage'));
        this.reload();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.approveFailed'))
    });
  }

  reject(memberId: number): void {
    this.leagueService.rejectMember(this.leagueId, memberId).subscribe({
      next: () => {
        this.toastService.info(this.t('league.members.rejectedTitle'), this.t('league.members.rejectedMessage'));
        this.reload();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.rejectFailed'))
    });
  }

  private reload(): void {
    this.leagueService.members(this.leagueId).subscribe({
      next: (members) => (this.members = members),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.membersFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
