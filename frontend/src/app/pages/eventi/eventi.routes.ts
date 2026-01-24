import { Routes } from '@angular/router';

export const EVENTI_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./eventi.component').then(c => c.EventiComponent)
  },
  {
    path: ':id',
    loadComponent: () => import('./evento-singolo/evento-singolo.component').then(c => c.EventoSingoloComponent)
  }
];
