import {Component} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {TranslatePipe, TranslateService} from '@ngx-translate/core';

import {ImportResponse} from '../../core/models/import.models';
import {ImportService} from '../../core/services/import.service';
import {ToastService} from '../../shared/services/toast.service';

@Component({
  selector: 'app-race-results-import',
  imports: [RouterLink, TranslatePipe],
  templateUrl: './race-results-import.component.html',
  styleUrl: './race-results-import.component.scss'
})
export class RaceResultsImportComponent {
  raceId = 0;
  importResult?: ImportResponse;
  uploading = false;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly importService: ImportService,
    private readonly toastService: ToastService,
    private readonly translateService: TranslateService
  ) {
    this.raceId = Number(this.route.snapshot.paramMap.get('raceId'));
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }
    this.uploading = true;
    this.importService.upload(this.raceId, file).subscribe({
      next: (res) => {
        this.importResult = res;
        this.uploading = false;
        this.toastService.success(this.t('import.createdTitle'), this.t('import.createdMessage'));
      },
      error: () => {
        this.uploading = false;
        this.toastService.error(this.t('toast.requestFailedTitle'), this.t('import.errors.uploadFailed'));
      }
    });
  }

  confirm(): void {
    if (!this.importResult) {
      return;
    }
    this.importService.confirm(this.importResult.id).subscribe({
      next: (res) => {
        this.importResult = res;
        this.toastService.success(this.t('import.confirmedTitle'), this.t('import.confirmedMessage'));
      },
      error: () => this.toastService.error(this.t('toast.requestFailedTitle'), this.t('import.errors.confirmFailed'))
    });
  }

  templateUrl(): string {
    return this.importService.templateUrl();
  }

  private t(key: string): string {
    return this.translateService.instant(key);
  }
}
