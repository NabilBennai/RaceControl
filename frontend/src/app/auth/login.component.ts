import {Component} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faEnvelope, faLock, faRightToBracket} from '@fortawesome/free-solid-svg-icons';
import {Router, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {AuthService} from '../core/services/auth.service';
import {ToastService} from '../shared/services/toast.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, TranslatePipe, FaIconComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  readonly form: FormGroup;
  readonly icons = {email: faEnvelope, password: faLock, submit: faRightToBracket};

  constructor(
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.form = this.fb.nonNullable.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.toastService.info(this.t('toast.formInvalidTitle'), this.t('toast.formInvalidMessage'));
      return;
    }

    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => {
        this.toastService.success(this.t('toast.loginSuccessTitle'), this.t('toast.loginSuccessMessage'));
        this.router.navigateByUrl('/app');
      },
      error: () => this.toastService.error(this.t('toast.loginErrorTitle'), this.t('toast.loginErrorMessage'))
    });
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
