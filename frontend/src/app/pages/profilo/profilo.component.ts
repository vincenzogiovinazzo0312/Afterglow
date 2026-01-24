import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { ProfiloService } from '../../service/profilo.service';

@Component({
  selector: 'app-profilo',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profilo.component.html',
  styleUrls: ['./profilo.component.css']
})
export class ProfiloComponent implements OnInit {

  // Dati utente
  utente = {
    username: '',
    nome: '',
    cognome: '',
    email: '',
    telefono: ''
  };

  // Form modifica dati
  isEditingDati = false;
  datiForm = {
    nome: '',
    cognome: '',
    email: '',
    telefono: ''
  };

  // Form cambio password
  isEditingPassword = false;
  passwordForm = {
    vecchiaPassword: '',
    nuovaPassword: '',
    confermaNuovaPassword: ''
  };

  // Messaggi
  successMessage = '';
  errorMessage = '';
  isLoading = false;

  constructor(
    public authService: AuthService,
    private profiloService: ProfiloService
  ) {}

  ngOnInit(): void {
    this.caricaDatiUtente();
  }

  caricaDatiUtente(): void {
    const username = this.authService.getUsername();
    if (username) {
      this.profiloService.getDatiUtente(username).subscribe({
        next: (data) => {
          this.utente = data;
          // Prepopola il form con i dati attuali
          this.datiForm = { ...data };
        },
        error: (error) => {
          console.error('Errore caricamento dati:', error);
          this.errorMessage = 'Errore nel caricamento dei dati';
        }
      });
    }
  }

  // Attiva/disattiva modifica dati
  toggleEditDati(): void {
    this.isEditingDati = !this.isEditingDati;
    if (this.isEditingDati) {
      // Ripristina i valori originali
      this.datiForm = { ...this.utente };
      this.clearMessages();
    }
  }

  // Salva modifiche dati
  salvaDati(): void {
    this.isLoading = true;
    this.clearMessages();

    this.profiloService.aggiornaDati(this.authService.getUsername()!, this.datiForm).subscribe({
      next: (response) => {
        this.utente = { ...this.utente, ...this.datiForm };
        this.successMessage = 'Dati aggiornati con successo!';
        this.isEditingDati = false;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Errore aggiornamento:', error);
        this.errorMessage = error.error?.message || 'Errore durante l\'aggiornamento';
        this.isLoading = false;
      }
    });
  }

  // Attiva/disattiva cambio password
  toggleEditPassword(): void {
    this.isEditingPassword = !this.isEditingPassword;
    if (this.isEditingPassword) {
      this.passwordForm = {
        vecchiaPassword: '',
        nuovaPassword: '',
        confermaNuovaPassword: ''
      };
      this.clearMessages();
    }
  }

  // Cambia password
  cambiaPassword(): void {
    // Validazione
    if (this.passwordForm.nuovaPassword !== this.passwordForm.confermaNuovaPassword) {
      this.errorMessage = 'Le nuove password non coincidono';
      return;
    }

    if (this.passwordForm.nuovaPassword.length < 8) {
      this.errorMessage = 'La password deve essere di almeno 8 caratteri';
      return;
    }

    this.isLoading = true;
    this.clearMessages();

    this.profiloService.cambiaPassword(
      this.authService.getUsername()!,
      this.passwordForm.vecchiaPassword,
      this.passwordForm.nuovaPassword
    ).subscribe({
      next: (response) => {
        this.successMessage = 'Password cambiata con successo!';
        this.isEditingPassword = false;
        this.isLoading = false;
        this.passwordForm = {
          vecchiaPassword: '',
          nuovaPassword: '',
          confermaNuovaPassword: ''
        };
      },
      error: (error) => {
        console.error('Errore cambio password:', error);
        this.errorMessage = error.error?.message || 'Errore durante il cambio password';
        this.isLoading = false;
      }
    });
  }

  annullaModifiche(): void {
    this.isEditingDati = false;
    this.isEditingPassword = false;
    this.clearMessages();
  }

  clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
