import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faEnvelope, faLock, faUser, faUserPlus} from '@fortawesome/free-solid-svg-icons';
import {Router} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {AuthService} from '../core/services/auth.service';
import {ToastService} from '../shared/services/toast.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, TranslatePipe, FaIconComponent],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {
  readonly form: FormGroup;
  readonly icons = {username: faUser, email: faEnvelope, password: faLock, submit: faUserPlus};

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('toast.formInvalidMessage'));
      return;
    }

    this.authService.register(this.form.getRawValue()).subscribe({
      next: () => {
        this.toastService.success(this.t('toast.registerSuccessTitle'), this.t('toast.registerSuccessMessage'));
        this.router.navigateByUrl('/app');
      },
      error: () => this.toastService.error(this.t('toast.registerErrorTitle'), this.t('toast.registerErrorMessage'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
