import {bootstrapApplication} from '@angular/platform-browser';

import {AppComponent} from './app/app.component';
import {appConfig} from './app/app.config';

const storedTheme = localStorage.getItem('racecontrol-theme');
const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
const activeTheme = storedTheme ?? (prefersDark ? 'dark' : 'light');
document.documentElement.classList.toggle('dark', activeTheme === 'dark');

bootstrapApplication(AppComponent, appConfig).catch((err: unknown) => {
  console.error('Echec du bootstrap Angular', err);
});