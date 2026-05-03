import {Component, Input} from '@angular/core';
import {RouterLink} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';

import {LeagueResponse} from '../../../core/models/league.models';

@Component({
  selector: 'app-league-card',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './league-card.component.html',
  styleUrl: './league-card.component.scss'
})
export class LeagueCardComponent {
  @Input({required: true}) league!: LeagueResponse;
}
