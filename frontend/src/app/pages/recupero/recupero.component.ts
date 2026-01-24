import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { FormsModule } from '@angular/forms';
import emailjs from '@emailjs/browser';

@Component({
  standalone: true,
  selector: 'app-recupero',
  templateUrl: './recupero.component.html',
  styleUrls: ['./recupero.component.css'],
  imports: [CommonModule, FormsModule, RouterLink]
})
export class RecuperoComponent {
  credentials = { username: '' };
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    emailjs.init('orXEGyJ2mrr8RIabx');
  }

  recupero() {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.credentials.username) {
      this.errorMessage = 'Inserisci un username';
      return;
    }

    this.isLoading = true;

    this.authService.forgotPassword(this.credentials.username).subscribe({
      next: (response) => {
        console.log('Response dal backend:', response);

        if (response.success) {
          this.sendEmail(response.email, response.otp, response.time);
        } else {
          this.errorMessage = response.message || 'Errore durante la richiesta';
          this.isLoading = false;
        }
      },
      error: (error) => {
        console.error('Errore dal backend:', error);
        this.errorMessage = error.error?.message || 'Utente non trovato';
        this.isLoading = false;
      }
    });
  }

  private sendEmail(toEmail: string, otp: string, time: string) {
    const serviceID = 'service_0y4ibhm';
    const templateID = 'template_l6k1cdd';

    const templateParams = {
      email: toEmail,
      passcode: otp,
      time: time
    };

    console.log('Invio email a:', toEmail);
    console.log('Con parametri:', templateParams);

    emailjs.send(serviceID, templateID, templateParams)
      .then((result) => {
        console.log('✅ Email inviata con successo!', result.text);
        this.successMessage = 'Email inviata! Controlla la tua casella di posta.';
        this.isLoading = false;

        setTimeout(() => {
          this.router.navigate(['/reset-password'], {
            queryParams: { username: this.credentials.username }
          });
        }, 2000);
      })
      .catch((error) => {
        console.error('❌ Errore invio email:', error);
        this.errorMessage = 'Errore durante l\'invio dell\'email: ' + error.text;
        this.isLoading = false;
      });
  }
}
