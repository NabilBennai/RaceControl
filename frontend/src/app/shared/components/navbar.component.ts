import {Component} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {TranslatePipe} from '@ngx-translate/core';

import {AuthService} from '../../core/services/auth.service';
import {I18nService} from '../services/i18n.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, TranslatePipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly i18nService: I18nService
  ) {
  }

  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  get isAdmin(): boolean {
    return this.authService.hasAdminRole();
  }

  get currentLang(): string {
    return this.i18nService.currentLang;
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigateByUrl('/login'),
      error: () => this.router.navigateByUrl('/login')
    });
  }

  toggleLanguage(): void {
    const nextLang = this.currentLang === 'fr' ? 'en' : 'fr';
    this.i18nService.setLang(nextLang);
  }
}
