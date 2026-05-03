import {Component} from '@angular/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {
  faGaugeHigh,
  faLanguage,
  faMoon,
  faRightFromBracket,
  faShieldHalved,
  faSun,
  faUserPlus
} from '@fortawesome/free-solid-svg-icons';
import {Router, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {AuthService} from '../../core/services/auth.service';
import {I18nService} from '../services/i18n.service';
import {ToastService} from '../services/toast.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, TranslatePipe, FaIconComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {
  readonly icons = {
    dashboard: faGaugeHigh,
    admin: faShieldHalved,
    logout: faRightFromBracket,
    themeDark: faMoon,
    themeLight: faSun,
    lang: faLanguage,
    register: faUserPlus
  };

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly i18nService: I18nService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
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

  get isDarkMode(): boolean {
    return document.documentElement.classList.contains('dark');
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.toastService.info(this.t('toast.logoutInfoTitle'), this.t('toast.logoutInfoMessage'));
        this.router.navigateByUrl('/login');
      },
      error: () => {
        this.toastService.error(this.t('toast.logoutErrorTitle'), this.t('toast.logoutErrorMessage'));
        this.router.navigateByUrl('/login');
      }
    });
  }

  toggleLanguage(): void {
    const nextLang = this.currentLang === 'fr' ? 'en' : 'fr';
    this.i18nService.setLang(nextLang);
  }

  toggleTheme(): void {
    const isDark = document.documentElement.classList.toggle('dark');
    localStorage.setItem('racecontrol-theme', isDark ? 'dark' : 'light');
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
