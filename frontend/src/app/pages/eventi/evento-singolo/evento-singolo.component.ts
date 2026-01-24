import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, DatePipe } from '@angular/common';
import { EventoService } from '../../../service/evento.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-evento-singolo',
  standalone: true,
  imports: [FormsModule, DatePipe, CommonModule],
  templateUrl: './evento-singolo.component.html',
  styleUrls: ['./evento-singolo.component.css']
})
export class EventoSingoloComponent implements OnInit {
  event: any = null;
  menuOpen = false;
  form = { nome: '', cognome: '', telefono: '' };
  successMessage = '';
  errorMessage = '';
  registrationAllowed = true;
  loading = true;
  isLoggedIn = false;
  isIscritto = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventoService: EventoService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      // Verifica se utente loggato
      this.isLoggedIn = this.authService.isLoggedIn();

      // Carica evento
      this.eventoService.getEventoById(Number(id)).subscribe({
        next: (data) => {
          this.event = data;
          this.loading = false;
          this.checkRegistration();
          this.checkIscrizione();
        },
        error: (error) => {
          console.error('Errore caricamento evento:', error);
          this.loading = false;
          this.errorMessage = 'Impossibile caricare l\'evento';
        }
      });
    }
  }

  //Verifica se utente è già iscritto
  checkIscrizione() {
    if (!this.isLoggedIn || !this.event) return;

    this.eventoService.verificaIscrizioneUtente(this.event.id).subscribe({
      next: (response) => {
        this.isIscritto = response.iscritto;
        console.log('✅ Check iscrizione:', this.isIscritto);
      },
      error: (err) => {
        console.error('Errore verifica iscrizione:', err);
        this.isIscritto = false;
      }
    });
  }

  checkRegistration() {
    if (this.event) {
      this.eventoService.verificaIscrizioneConsentita(this.event.id).subscribe({
        next: (response) => {
          this.registrationAllowed = response.consentita;
        },
        error: (error) => {
          console.error('Errore verifica iscrizione:', error);
        }
      });
    }
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  iscrizioneRapida() {
    if (!this.event) {
      this.errorMessage = '';
      return;
    }

    //CONTROLLO SE L'UTENTE LOGGATO E' GIà ISCRITTO
    if (this.isIscritto) {
      this.successMessage = '✅ Sei già iscritto a questo evento!';
      this.errorMessage = '';
      return;
    }

    // Verifica se iscrizioni aperte
    if (!this.registrationAllowed) {
      this.errorMessage = 'Le iscrizioni non sono aperte';
      return;
    }

    this.successMessage = '';
    this.errorMessage = '';

    this.eventoService.iscrizioneRapida(this.event.id).subscribe({
      next: (response) => {
        console.log('Risposta iscrizione rapida:', response);
        this.successMessage = response.message || 'Iscrizione completata con successo!';
        this.errorMessage = '';
        this.isIscritto = true; // 🔥 AGGIORNA STATO
        this.registrationAllowed = false;
      },
      error: (error) => {
        console.error('Errore iscrizione rapida:', error);
        this.errorMessage = error.error?.error || error.error?.message || 'Errore durante l\'iscrizione';
        this.successMessage = '';
      }
    });
  }

  // Iscrizione manuale (form completo - utente non loggato)
  register() {
    if (!this.event) return;

    if (!this.form.nome || !this.form.cognome || !this.form.telefono) {
      this.errorMessage = 'Compila tutti i campi';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    const iscrizione = {
      eventoId: this.event.id,
      nome: this.form.nome,
      cognome: this.form.cognome,
      telefono: this.form.telefono
    };

    this.eventoService.iscrivitiEvento(iscrizione).subscribe({
      next: (response) => {
        this.successMessage = response.message || 'Registrazione completata con successo!';
        this.form = { nome: '', cognome: '', telefono: '' };
        this.registrationAllowed = false;
      },
      error: (error) => {
        this.errorMessage = error.error?.error || 'Errore durante la registrazione';
        console.error('Errore registrazione:', error);
      }
    });
  }
}
