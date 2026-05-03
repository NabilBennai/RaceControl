import {Component} from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {LeagueRequest} from '../../core/models/league.models';
import {LeagueService} from '../../core/services/league.service';
import {ToastService} from '../../shared/services/toast.service';
import {LeagueFormComponent} from './components/league-form.component';
import {LeagueHeaderComponent} from './components/league-header.component';

@Component({
  selector: 'app-league-create',
  imports: [LeagueFormComponent, LeagueHeaderComponent, RouterLink, TranslatePipe],
  templateUrl: './league-create.component.html',
  styleUrl: './league-create.component.scss'
})
export class LeagueCreateComponent {
  constructor(
    private readonly leagueService: LeagueService,
    private readonly router: Router,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
  }

  createLeague(payload: LeagueRequest): void {
    this.leagueService.create(payload).subscribe({
      next: (league) => {
        this.toastService.success(this.t('league.createdTitle'), this.t('league.createdMessage'));
        this.router.navigate(['/leagues', league.id]);
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('league.errors.createFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
