import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './registrazione.component.html',
  styleUrl:'./registrazione.component.css',
  imports: [CommonModule, FormsModule, RouterLink]
})
export class RegisterComponent {
  user = {
    username: '',
    nome: '',
    cognome: '',
    telefono: '',
    email: '',
    password: ''
  };

  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  // Errori specifici per campo
  emailError: string = '';
  passwordMatchError: string = '';

  // Indicatori di validità password
  passwordStrength = {
    minLength: false,
    hasUppercase: false,
    hasLowercase: false,
    hasNumber: false,
    hasSpecialChar: false
  };

  constructor(
      private authService: AuthService,
      private router: Router
  ) {}

  // Controlla la password mentre l'utente digita
  onPasswordChange() {
    this.passwordStrength.minLength = this.user.password.length >= 8;
    this.passwordStrength.hasUppercase = /[A-Z]/.test(this.user.password);
    this.passwordStrength.hasLowercase = /[a-z]/.test(this.user.password);
    this.passwordStrength.hasNumber = /[0-9]/.test(this.user.password);
    this.passwordStrength.hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(this.user.password);

    // Ricontrolla la corrispondenza se confirmPassword è già stato inserito
    if (this.confirmPassword) {
      this.validatePasswordMatch();
    }
  }

  // Valida l'email quando l'utente esce dal campo
  validateEmail() {
    if (!this.user.email) {
      this.emailError = '';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.user.email)) {
      this.emailError = 'Email non valida';
    } else {
      this.emailError = '';
    }
  }

  // Valida la corrispondenza delle password
  validatePasswordMatch() {
    if (!this.confirmPassword) {
      this.passwordMatchError = '';
      return;
    }

    if (this.user.password !== this.confirmPassword) {
      this.passwordMatchError = 'Le password non coincidono';
    } else {
      this.passwordMatchError = '';
    }
  }

  isPasswordValid(): boolean {
    return Object.values(this.passwordStrength).every(v => v === true);
  }

  isFormValid(): boolean {
    return this.user.username !== '' &&
        this.user.nome !== '' &&
        this.user.cognome !== '' &&
        this.user.email !== '' &&
        this.user.password !== '' &&
        this.confirmPassword !== '' &&
        this.isPasswordValid() &&
        !this.emailError &&
        !this.passwordMatchError;
  }

  register() {
    this.errorMessage = '';
    this.successMessage = '';

    // Validazione finale
    this.validateEmail();
    this.validatePasswordMatch();

    // Controllo campi obbligatori
    if (!this.user.username || !this.user.password ||
        !this.user.nome || !this.user.cognome ||
        !this.user.email) {
      this.errorMessage = 'Compila tutti i campi obbligatori';
      return;
    }

    // Controllo errori nei campi
    if (this.emailError || this.passwordMatchError) {
      this.errorMessage = 'Correggi gli errori nel form';
      return;
    }

    if (!this.isPasswordValid()) {
      this.errorMessage = 'La password non soddisfa tutti i requisiti di sicurezza';
      return;
    }

    this.isLoading = true;

    this.authService.register(this.user).subscribe({
      next: (response) => {
        console.log('Registrazione OK:', response);
        this.successMessage = 'Registrazione completata con successo! Reindirizzamento al login...';

        // Dopo 2 secondi reindirizza al login
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (error) => {
        console.error('Errore registrazione:', error);
        this.errorMessage = error.error?.message || 'Errore durante la registrazione';
        this.isLoading = false;
      }
    });
  }
}
