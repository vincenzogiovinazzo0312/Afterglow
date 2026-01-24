import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],  // CORRETTO
  imports: [CommonModule, FormsModule, RouterLink]
})
export class ResetPasswordComponent implements OnInit {
  username: string = '';
  otp: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  // Errore specifico
  passwordMatchError: string = '';

  // Indicatori di validità
  passwordStrength = {
    minLength: false,
    hasUppercase: false,
    hasLowercase: false,
    hasNumber: false,
    hasSpecialChar: false
  };

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.username = params['username'] || '';
    });
  }

  onPasswordChange() {
    this.passwordStrength.minLength = this.newPassword.length >= 8;
    this.passwordStrength.hasUppercase = /[A-Z]/.test(this.newPassword);
    this.passwordStrength.hasLowercase = /[a-z]/.test(this.newPassword);
    this.passwordStrength.hasNumber = /[0-9]/.test(this.newPassword);
    this.passwordStrength.hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(this.newPassword);

    if (this.confirmPassword) {
      this.validatePasswordMatch();
    }
  }

  validatePasswordMatch() {
    if (!this.confirmPassword) {
      this.passwordMatchError = '';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.passwordMatchError = 'Le password non coincidono';
    } else {
      this.passwordMatchError = '';
    }
  }

  isPasswordValid(): boolean {
    return Object.values(this.passwordStrength).every(v => v === true);
  }

  isFormValid(): boolean {
    return this.username !== '' &&
      this.otp !== '' &&
      this.newPassword !== '' &&
      this.confirmPassword !== '' &&
      this.isPasswordValid() &&
      !this.passwordMatchError;
  }

  resetPassword() {
    this.errorMessage = '';
    this.successMessage = '';

    this.validatePasswordMatch();

    if (!this.username || !this.otp || !this.newPassword) {
      this.errorMessage = 'Compila tutti i campi';
      return;
    }

    if (this.passwordMatchError) {
      this.errorMessage = 'Correggi gli errori nel form';
      return;
    }

    if (!this.isPasswordValid()) {
      this.errorMessage = 'La password non soddisfa i requisiti di sicurezza';
      return;
    }

    this.isLoading = true;

    this.authService.resetPassword(this.username, this.otp, this.newPassword).subscribe({
      next: (response) => {
        if (response.success) {
          this.successMessage = 'Password aggiornata con successo!';

          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        } else {
          this.errorMessage = response.message || 'Errore durante il reset';
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Errore:', error);
        this.errorMessage = error.error?.message || 'OTP non valido o scaduto';
        this.isLoading = false;
      }
    });
  }
}
