import {Routes} from '@angular/router';

import {LoginComponent} from './auth/login.component';
import {RegisterComponent} from './auth/register.component';
import {adminGuard} from './core/guards/admin.guard';
import {authGuard} from './core/guards/auth.guard';
import {AppLayoutComponent} from './layout/app-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {path: '', redirectTo: 'login', pathMatch: 'full'},
      {path: 'login', component: LoginComponent},
      {path: 'register', component: RegisterComponent},
      {
        path: 'app',
        canActivate: [authGuard],
        loadComponent: () => import('./layout/private-layout.component').then((m) => m.PrivateLayoutComponent)
      },
      {
        path: 'admin',
        canActivate: [authGuard, adminGuard],
        loadComponent: () => import('./layout/admin-layout.component').then((m) => m.AdminLayoutComponent)
      }
    ]
  },
  {path: '**', redirectTo: 'login'}
];
