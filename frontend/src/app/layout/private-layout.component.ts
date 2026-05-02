import {Component} from '@angular/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faGaugeHigh} from '@fortawesome/free-solid-svg-icons';
import {TranslatePipe} from '@ngx-translate/core';

@Component({
  selector: 'app-private-layout',
  standalone: true,
  imports: [TranslatePipe, FaIconComponent],
  templateUrl: './private-layout.component.html',
  styleUrl: './private-layout.component.scss'
})
export class PrivateLayoutComponent {
  readonly dashboardIcon = faGaugeHigh;
}