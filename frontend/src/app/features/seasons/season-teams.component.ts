import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {LeagueMemberResponse} from '../../core/models/league.models';
import {SeasonResponse} from '../../core/models/season.models';
import {TeamRequest, TeamResponse} from '../../core/models/team.models';
import {LeagueService} from '../../core/services/league.service';
import {SeasonService} from '../../core/services/season.service';
import {TeamService} from '../../core/services/team.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-season-teams',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe],
  templateUrl: './season-teams.component.html',
  styleUrl: './season-teams.component.scss'
})
export class SeasonTeamsComponent implements OnInit {
  season?: SeasonResponse;
  teams: TeamResponse[] = [];
  eligibleDrivers: LeagueMemberResponse[] = [];
  form: FormGroup;
  editForm: FormGroup;
  editingTeamId?: number;

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly seasonService: SeasonService,
    private readonly teamService: TeamService,
    private readonly leagueService: LeagueService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
      color: ['#E10600', [Validators.required, Validators.pattern(/^#?[0-9A-Fa-f]{6}$/)]],
      logoUrl: ['']
    });
    this.editForm = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
      color: ['#E10600', [Validators.required, Validators.pattern(/^#?[0-9A-Fa-f]{6}$/)]],
      logoUrl: ['']
    });
  }

  ngOnInit(): void {
    const seasonId = Number(this.route.snapshot.paramMap.get('seasonId'));
    this.seasonService.getById(seasonId).subscribe({
      next: (season) => {
        this.season = season;
        this.loadTeams();
        this.loadEligibleDrivers();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.loadFailed'))
    });
  }

  createTeam(): void {
    if (!this.season || this.form.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('team.errors.formInvalid'));
      return;
    }
    this.teamService.create(this.season.id, this.toRequest(this.form)).subscribe({
      next: () => {
        this.form.reset({name: '', color: '#E10600', logoUrl: ''});
        this.toastService.success(this.t('team.createdTitle'), this.t('team.createdMessage'));
        this.loadTeams();
        this.loadEligibleDrivers();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.createFailed'))
    });
  }

  startEdit(team: TeamResponse): void {
    this.editingTeamId = team.id;
    this.editForm.patchValue({
      name: team.name,
      color: team.color,
      logoUrl: team.logoUrl ?? ''
    });
  }

  cancelEdit(): void {
    this.editingTeamId = undefined;
  }

  updateTeam(teamId: number): void {
    if (this.editForm.invalid) {
      return;
    }
    this.teamService.update(teamId, this.toRequest(this.editForm)).subscribe({
      next: () => {
        this.toastService.success(this.t('team.updatedTitle'), this.t('team.updatedMessage'));
        this.editingTeamId = undefined;
        this.loadTeams();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.updateFailed'))
    });
  }

  deleteTeam(teamId: number): void {
    this.teamService.delete(teamId).subscribe({
      next: () => {
        this.toastService.success(this.t('team.deletedTitle'), this.t('team.deletedMessage'));
        this.loadTeams();
        this.loadEligibleDrivers();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.deleteFailed'))
    });
  }

  addMember(teamId: number, userIdRaw: string): void {
    const userId = Number(userIdRaw);
    if (!userId) {
      return;
    }
    this.teamService.addMember(teamId, {userId}).subscribe({
      next: () => {
        this.toastService.success(this.t('team.memberAddedTitle'), this.t('team.memberAddedMessage'));
        this.loadTeams();
        this.loadEligibleDrivers();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.addMemberFailed'))
    });
  }

  removeMember(teamId: number, memberId: number): void {
    this.teamService.removeMember(teamId, memberId).subscribe({
      next: () => {
        this.toastService.success(this.t('team.memberRemovedTitle'), this.t('team.memberRemovedMessage'));
        this.loadTeams();
        this.loadEligibleDrivers();
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.removeMemberFailed'))
    });
  }

  isAssigned(userId: number): boolean {
    return this.teams.some(team => team.members.some(member => member.userId === userId));
  }

  private toRequest(form: FormGroup): TeamRequest {
    const raw = form.getRawValue() as { name: string; color: string; logoUrl: string };
    return {
      name: raw.name,
      color: raw.color,
      logoUrl: raw.logoUrl?.trim() ? raw.logoUrl.trim() : null
    };
  }

  private loadTeams(): void {
    if (!this.season) {
      return;
    }
    this.teamService.listBySeason(this.season.id).subscribe({
      next: (teams) => (this.teams = teams),
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.listFailed'))
    });
  }

  private loadEligibleDrivers(): void {
    if (!this.season) {
      return;
    }
    this.leagueService.members(this.season.leagueId).subscribe({
      next: (members) => {
        this.eligibleDrivers = members.filter(member => member.status === 'APPROVED');
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('team.errors.membersLoadFailed'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
