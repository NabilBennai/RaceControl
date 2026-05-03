import {Component} from '@angular/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faCalendarDays, faFlagCheckered, faScaleBalanced} from '@fortawesome/free-solid-svg-icons';
import {RouterLink} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [TranslatePipe, RouterLink, FaIconComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {
  readonly icons = {
    calendar: faCalendarDays,
    race: faFlagCheckered,
    steward: faScaleBalanced
  };
}