import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  imports: [CommonModule, FormsModule, RouterLink]
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  errorMessage = '';
  isLoading = false;

  constructor(
      private authService: AuthService,
      private router: Router,
  ) {}

  login(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.credentials).subscribe({
      next: (response) => {
        console.log('Login effettuato:', response);

        // Reindirizza in base al ruolo
        if (response.role === 'ADMIN') {
          this.router.navigate(['/admin-home']);
        } else if (response.role === 'USER') {
          this.router.navigate(['/']);
        } else {
          // Fallback se il ruolo non è riconosciuto
          this.router.navigate(['/']);
        }
      },
      error: (error) => {
        console.error('Errore login:', error);
        this.errorMessage = error.error?.message || 'Credenziali non valide. Riprova.';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}

