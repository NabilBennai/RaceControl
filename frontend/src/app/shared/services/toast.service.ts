import {Injectable} from '@angular/core';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastItem {
  id: number;
  type: ToastType;
  title: string;
  message?: string;
  durationMs: number;
}

@Injectable({providedIn: 'root'})
export class ToastService {
  private readonly items: ToastItem[] = [];
  private nextId = 1;

  get toasts(): ToastItem[] {
    return this.items;
  }

  show(type: ToastType, title: string, message?: string, durationMs = 4500): void {
    const toast: ToastItem = {id: this.nextId++, type, title, message, durationMs};
    this.items.push(toast);
    setTimeout(() => this.dismiss(toast.id), durationMs);
  }

  success(title: string, message?: string, durationMs?: number): void {
    this.show('success', title, message, durationMs);
  }

  error(title: string, message?: string, durationMs?: number): void {
    this.show('error', title, message, durationMs ?? 6000);
  }

  info(title: string, message?: string, durationMs?: number): void {
    this.show('info', title, message, durationMs);
  }

  dismiss(id: number): void {
    const index = this.items.findIndex((t) => t.id === id);
    if (index >= 0) {
      this.items.splice(index, 1);
    }
  }
}