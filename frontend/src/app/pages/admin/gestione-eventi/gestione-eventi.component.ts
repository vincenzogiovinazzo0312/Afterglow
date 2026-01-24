import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { EventoService } from '../../../service/evento.service';
import { Evento } from '../../../models/evento.model';

@Component({
  selector: 'app-gestione-eventi',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './gestione-eventi.component.html',
  styleUrl: './gestione-eventi.component.css'
})
export class GestioneEventiComponent implements OnInit {
  listaEventi: Evento[] = [];
  fileSelezionato: File | null = null;
  eventoCorrente: Evento = this.nuovoEvento();
  uploading = false;

  constructor(private eventoService: EventoService) {}

  ngOnInit(): void {
    this.caricaEventi();
  }

  // ===== CARICA EVENTI DAL DATABASE =====
  caricaEventi(): void {
    this.eventoService.getEventiProssimi().subscribe({
      next: (eventi) => {
        console.log('RAW eventi dal backend:', eventi);
        this.listaEventi = eventi;
      },
      error: (err) => {
        console.error('Errore caricamento eventi:', err);
        alert('Errore nel caricamento degli eventi');
      }
    });
  }

  // ===== GESTIONE FILE (anteprima immagine) =====
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.fileSelezionato = input.files[0];

      const reader = new FileReader();
      reader.onload = (e: ProgressEvent<FileReader>) => {
        this.eventoCorrente.immagine = e.target?.result as string;
      };
      reader.readAsDataURL(this.fileSelezionato);
    }
  }

  // ===== SALVA EVENTO (crea o modifica) =====
  salvaEvento(): void {
    if (!this.validaForm()) {
      alert('Compila tutti i campi obbligatori (Titolo, Descrizione, Data)');
      return;
    }

    if (this.eventoCorrente.id === 0) {
      if (!this.fileSelezionato) {
        alert('Seleziona un\'immagine per il nuovo evento.');
        return;
      }

      const formData = new FormData();
      formData.append('file', this.fileSelezionato);
      formData.append('titolo', this.eventoCorrente.titolo);
      formData.append('descrizione', this.eventoCorrente.descrizione || '');
      formData.append('data', this.eventoCorrente.data);

      this.uploading = true;
      this.eventoService.uploadEvento(formData).subscribe({
        next: (res) => {
          this.uploading = false;
          console.log('Evento creato con upload:', res);
          alert('Evento creato con successo!');
          this.pulisciForm();
          this.caricaEventi();
        },
        error: (err) => {
          this.uploading = false;
          console.error('Errore salvataggio (upload):', err);
          alert('Errore durante il salvataggio. Controlla la console.');
        }
      });

    } else {
      const dto: Evento = {
        id: this.eventoCorrente.id,
        titolo: this.eventoCorrente.titolo,
        descrizione: this.eventoCorrente.descrizione,
        data: this.eventoCorrente.data,
        immagine: this.eventoCorrente.immagine
      };

      this.eventoService.aggiornaEvento(dto.id, dto).subscribe({
        next: () => {
          alert('Evento aggiornato con successo!');
          this.pulisciForm();
          this.caricaEventi();
        },
        error: (err) => {
          console.error('Errore aggiornamento:', err);
          alert('Errore durante l\'aggiornamento. Controlla la console.');
        }
      });
    }
  }

  // ===== PREPARA MODIFICA EVENTO =====
  preparaModifica(evento: Evento): void {
    this.eventoCorrente = { ...evento };
    this.fileSelezionato = null;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // ===== ELIMINA EVENTO =====
  eliminaEvento(id: number): void {
    if (!confirm('Sei sicuro di voler eliminare questo evento?')) {
      return;
    }

    this.eventoService.eliminaEvento(id).subscribe({
      next: () => {
        alert('Evento eliminato con successo!');
        this.caricaEventi();
      },
      error: (err) => {
        console.error('Errore eliminazione:', err);
        alert('Errore durante l\'eliminazione');
      }
    });
  }

  // ===== UTILITY =====
  private nuovoEvento(): Evento {
    return {
      id: 0,
      titolo: '',
      descrizione: '',
      data: '',
      immagine: ''
    };
  }

  pulisciForm(): void {
    this.eventoCorrente = this.nuovoEvento();
    this.fileSelezionato = null;
  }

  private validaForm(): boolean {
    return !!(
      this.eventoCorrente.titolo.trim() &&
      this.eventoCorrente.descrizione?.trim() &&
      this.eventoCorrente.data
    );
  }
}
