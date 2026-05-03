import {Component, Input} from '@angular/core';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-league-header',
  imports: [TranslatePipe],
  templateUrl: './league-header.component.html',
  styleUrl: './league-header.component.scss'
})
export class LeagueHeaderComponent {
  @Input({required: true}) title!: string;
  @Input() subtitle = '';
}
