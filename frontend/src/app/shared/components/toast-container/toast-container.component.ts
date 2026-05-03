import {Component} from '@angular/core';
import {FaIconComponent} from '@fortawesome/angular-fontawesome';
import {faCheckCircle, faCircleInfo, faTriangleExclamation, faXmark} from '@fortawesome/free-solid-svg-icons';

import {ToastService} from '../../services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [FaIconComponent],
  templateUrl: './toast-container.component.html',
  styleUrl: './toast-container.component.scss'
})
export class ToastContainerComponent {
  readonly icons = {
    success: faCheckCircle,
    error: faTriangleExclamation,
    info: faCircleInfo,
    close: faXmark
  };

  constructor(public readonly toastService: ToastService) {
  }
}