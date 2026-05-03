import {Routes} from '@angular/router';

import {LoginComponent} from './auth/login.component';
import {RegisterComponent} from './auth/register.component';
import {adminGuard} from './core/guards/admin.guard';
import {anonOnlyGuard} from './core/guards/anon-only.guard';
import {authGuard} from './core/guards/auth.guard';
import {AppLayoutComponent} from './layout/app-layout.component';

export const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      {
        path: '',
        canActivate: [anonOnlyGuard],
        loadComponent: () => import('./features/home/home.component').then((m) => m.HomeComponent)
      },
      {path: 'login', canActivate: [anonOnlyGuard], component: LoginComponent},
      {path: 'register', canActivate: [anonOnlyGuard], component: RegisterComponent},
      {
        path: 'app',
        canActivate: [authGuard],
        loadComponent: () => import('./layout/private-layout.component').then((m) => m.PrivateLayoutComponent)
      },
      {
        path: 'dashboard',
        canActivate: [authGuard],
        loadComponent: () => import('./layout/private-layout.component').then((m) => m.PrivateLayoutComponent)
      },
      {
        path: 'leagues',
        canActivate: [authGuard],
        loadComponent: () => import('./features/leagues/leagues-list.component').then((m) => m.LeaguesListComponent)
      },
      {
        path: 'leagues/new',
        canActivate: [authGuard],
        loadComponent: () => import('./features/leagues/league-create.component').then((m) => m.LeagueCreateComponent)
      },
      {
        path: 'leagues/:id',
        canActivate: [authGuard],
        loadComponent: () => import('./features/leagues/league-detail.component').then((m) => m.LeagueDetailComponent)
      },
      {
        path: 'leagues/:id/join',
        canActivate: [authGuard],
        loadComponent: () => import('./features/leagues/league-join.component').then((m) => m.LeagueJoinComponent)
      },
      {
        path: 'leagues/:id/members',
        canActivate: [authGuard],
        loadComponent: () => import('./features/leagues/league-members.component').then((m) => m.LeagueMembersComponent)
      },
      {
        path: 'leagues/:leagueId/seasons',
        canActivate: [authGuard],
        loadComponent: () => import('./features/seasons/leagues-seasons.component').then((m) => m.LeaguesSeasonsComponent)
      },
      {
        path: 'seasons/:seasonId',
        canActivate: [authGuard],
        loadComponent: () => import('./features/seasons/season-detail.component').then((m) => m.SeasonDetailComponent)
      },
      {
        path: 'seasons/:seasonId/teams',
        canActivate: [authGuard],
        loadComponent: () => import('./features/seasons/season-teams.component').then((m) => m.SeasonTeamsComponent)
      },
      {
        path: 'seasons/:seasonId/calendar',
        canActivate: [authGuard],
        loadComponent: () => import('./features/races/season-calendar.component').then((m) => m.SeasonCalendarComponent)
      },
      {
        path: 'races/:raceId',
        canActivate: [authGuard],
        loadComponent: () => import('./features/races/race-detail.component').then((m) => m.RaceDetailComponent)
      },
      {
        path: 'races/:raceId/results',
        canActivate: [authGuard],
        loadComponent: () => import('./features/results/race-results-view.component').then((m) => m.RaceResultsViewComponent)
      },
      {
        path: 'races/:raceId/results/edit',
        canActivate: [authGuard],
        loadComponent: () => import('./features/results/race-results-edit.component').then((m) => m.RaceResultsEditComponent)
      },
      {
        path: 'admin',
        canActivate: [authGuard, adminGuard],
        loadComponent: () => import('./layout/admin-layout.component').then((m) => m.AdminLayoutComponent)
      }
    ]
  },
  {path: '**', redirectTo: ''}
];
