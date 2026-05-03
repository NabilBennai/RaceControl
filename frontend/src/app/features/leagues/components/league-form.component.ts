import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';

import {GamePlatform, LeagueRequest, LeagueVisibility} from '../../../core/models/league.models';

@Component({
  selector: 'app-league-form',
  imports: [ReactiveFormsModule, TranslatePipe],
  templateUrl: './league-form.component.html',
  styleUrl: './league-form.component.scss'
})
export class LeagueFormComponent implements OnInit {
  @Input() initialValue?: LeagueRequest;
  @Input() submitLabel = 'league.create';
  @Output() submitted = new EventEmitter<LeagueRequest>();

  readonly platforms: GamePlatform[] = ['F1', 'ACC', 'IRACING', 'LMU', 'RF2', 'OTHER'];
  readonly visibilities: LeagueVisibility[] = ['PUBLIC', 'PRIVATE'];
  readonly form: FormGroup;

  constructor(private readonly fb: FormBuilder) {
    this.form = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(120)]],
      description: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
      gamePlatform: ['F1', Validators.required],
      visibility: ['PUBLIC', Validators.required]
    });
  }

  ngOnInit(): void {
    if (this.initialValue) {
      this.form.patchValue(this.initialValue);
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitted.emit(this.form.getRawValue() as LeagueRequest);
  }
}
