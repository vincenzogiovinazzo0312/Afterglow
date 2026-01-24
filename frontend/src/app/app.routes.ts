import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { HomeComponent } from './pages/home/home.component';
import { ChiSiamoComponent } from './pages/chi-siamo/chi-siamo.component';
import { RegisterComponent } from './pages/registrazione/registrazione.component';
import { LoginComponent } from './pages/login/login.component';
import { ContattiComponent } from './pages/contatti/contatti.component';
import { RecuperoComponent } from './pages/recupero/recupero.component';
import { ResetPasswordComponent } from './pages/recupero/reset-password.component';
import { AuthGuard } from './guards/auth.guards';
import {GestioneUtentiComponent} from './pages/admin/gestione-utenti/gestione-utenti.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      // Rotte pubbliche (accessibili a tutti)
      { path: '', component: HomeComponent },
      { path: 'chi-siamo', component: ChiSiamoComponent },
      { path: 'registrazione', component: RegisterComponent },
      { path: 'login', component: LoginComponent },
      { path: 'contatti', component: ContattiComponent },
      { path: 'recupero', component: RecuperoComponent },
      { path: 'reset-password', component: ResetPasswordComponent },

      // Rotta protetta: accessibile a USER e ADMIN loggati
      {
        path: 'profilo',
        loadComponent: () => import('./pages/profilo/profilo.component').then(m => m.ProfiloComponent),
        canActivate: [AuthGuard]
      },

      {
        path: 'eventi',
        loadChildren: () => import('./pages/eventi/eventi.routes').then(m => m.EVENTI_ROUTES)
      },

      {
        path: 'foto',
        loadComponent: () => import('./pages/foto/album.component').then(m => m.AlbumsComponent)
      },

      {
        path: 'foto/:id',
        loadComponent: () => import('./pages/foto/album-aperto/AlbumAperto.component').then(m => m.AlbumApertoComponent)
      },

      // Rotte protette ADMIN: accessibili solo agli amministratori

      {
        path: 'admin-home',
        loadComponent:() => import('./pages/admin/admin-home/admin-home.component').then(m => m.HomeAdminComponent),
        canActivate: [AuthGuard],
        data: {role:'ADMIN'}
      },
      {
        path: 'admin/gestisci-utenti',
        loadComponent: () => import('./pages/admin/gestione-utenti/gestione-utenti.component').then(m => m.GestioneUtentiComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },

      {
        path: 'admin/gestisci-foto',
        loadComponent: () => import('./pages/admin/gestisci-album/crea-album/crea-album.component').then(m => m.CreaAlbumComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },

      {
        path: 'admin/lista-album',
        loadComponent: () => import('./pages/admin/gestisci-album/lista-album/lista-album.component').then(m => m.ListaAlbumsComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },
      {
        path: 'admin/modifica-album/:id',
        loadComponent: () => import('./pages/admin/gestisci-album/modifica-album/modifica-album.component').then(m => m.ModificaAlbumComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },

      {
        path: 'admin/gestisci-eventi',
        loadComponent: () => import('./pages/admin/gestione-eventi/gestione-eventi.component').then(m => m.GestioneEventiComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },
      {
        path: 'admin/gestisci-lista',
        loadComponent: () => import('./pages/admin/gestione-liste/gestione-liste.component').then(m => m.GestioneListeComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },

      {
        path: 'admin/dettaglio-lista/:id',
        loadComponent: () => import('./pages/admin/gestione-liste/dettaglio-lista/dettaglio-lista.component').then(m => m.DettaglioListaComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },

      /*
      {
        path: 'admin/album/:id',
        loadComponent: () => import('./pages/admin/dettaglio-album/dettaglio-album.component').then(m => m.DettaglioAlbumComponent),
        canActivate: [AuthGuard],
        data: { role: 'ADMIN' }
      },


      // Pagina errore accesso negato
      {
        path: 'unauthorized',
        loadComponent: () => import('./pages/unauthorized/unauthorized.component').then(m => m.UnauthorizedComponent)
      },

      // Redirect per rotte non trovate
      { path: '**', redirectTo: '' }

       */
    ]
  }
];
