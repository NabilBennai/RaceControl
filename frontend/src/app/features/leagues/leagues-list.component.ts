import {Component, OnInit} from '@angular/core';
import {RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {LeagueResponse} from '../../core/models/league.models';
import {LeagueService} from '../../core/services/league.service';
import {LeagueCardComponent} from './components/league-card.component';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-leagues-list',
  imports: [TranslatePipe, RouterLink, LeagueCardComponent],
  templateUrl: './leagues-list.component.html',
  styleUrl: './leagues-list.component.scss'
})
export class LeaguesListComponent implements OnInit {
  leagues: LeagueResponse[] = [];

  constructor(
    private readonly leagueService: LeagueService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
  }

  ngOnInit(): void {
    this.leagueService.list().subscribe({
      next: (leagues) => (this.leagues = leagues),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.listFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
