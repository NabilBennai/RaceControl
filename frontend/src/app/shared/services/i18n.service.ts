import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

@Injectable({providedIn: 'root'})
export class I18nService {
  readonly supportedLanguages: Array<'fr' | 'en'> = ['fr', 'en'];

  constructor(private readonly translate: TranslateService) {
    this.translate.addLangs(this.supportedLanguages);
    this.translate.setDefaultLang('fr');
  }

  get currentLang(): string {
    return this.translate.currentLang || this.translate.defaultLang || 'fr';
  }

  setLang(lang: 'fr' | 'en'): void {
    this.translate.use(lang);
  }
}
