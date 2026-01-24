import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const isLoggedIn = this.authService.isLoggedIn();
    const requiredRole = route.data['role'];
    const userRole = this.authService.getRole();

    // Se non è loggato, reindirizza al login
    if (!isLoggedIn) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
      return false;
    }

    // Se non c'è un ruolo richiesto, basta essere loggati
    if (!requiredRole) {
      return true;
    }

    // Verifica se l'utente ha il ruolo richiesto
    if (userRole === requiredRole) {
      return true;
    }

    // Se è admin, può accedere a tutto
    if (userRole === 'ADMIN') {
      return true;
    }

    // Altrimenti reindirizza a una pagina non autorizzata
    this.router.navigate(['/unauthorized']);
    return false;
  }
}
