import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FeaturesComponent } from '../../components/features/features.component';
import { EventoService } from '../../service/evento.service';
import { Evento } from '../../models/evento.model';

@Component({
  selector: 'app-eventi',
  standalone: true,
  imports: [CommonModule, RouterModule, FeaturesComponent, HttpClientModule],
  templateUrl: './eventi.component.html',
  styleUrls: ['./eventi.component.css']
})
export class EventiComponent implements OnInit {
  isMenuOpen = false;
  events: Evento[] = [];
  loading = false;

  constructor(private eventoService: EventoService) {}

  toggleMenu() { this.isMenuOpen = !this.isMenuOpen; }
  closeMenu() { this.isMenuOpen = false; }

  loadEventi() {
    this.loading = true;
    console.log('🔄 Caricamento eventi...');
    this.eventoService.getEventiProssimi().subscribe({
      next: (data) => {
        console.log('✅ Eventi caricati:', data.length);
        this.events = data.map(e => ({
          ...e,
          dataFormattata: this.formatDate(e.data)
        }));
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento eventi:', error);
        this.loading = false;
      }
    });
  }

  iscriviti(eventoId: number) {
    // Verifica prima se l'utente è già iscritto
    this.eventoService.verificaIscrizioneUtente(eventoId).subscribe({
      next: (response) => {
        if (response.iscritto) {
          alert('✅ Sei già iscritto a questo evento!');
          return;
        }
        // Se non è iscritto procedi con iscrizione
        console.log('📝 Iscrizione rapida per evento:', eventoId);
        this.eventoService.iscrizioneRapida(eventoId).subscribe({
          next: (resp) => {
            console.log('✅ Iscrizione OK:', resp);
            alert('Iscritto con successo!');
            this.loadEventi();
          },
          error: (err) => {
            console.error('❌ Errore iscrizione:', err);
            alert('Errore: ' + (err.error?.error || 'Servizio non disponibile'));
          }
        });
      },
      error: (err) => {
        console.error('❌ Errore verifica iscrizione:', err);
        alert('Errore verifica iscrizione');
      }
    });
  }

  ngOnInit() {
    this.loadEventi();
  }

  formatDate(dateString: string): string {
    const data = new Date(dateString);
    const mese = data.toLocaleDateString('it-IT', { month: 'short' }).replace('.', '');
    const giorno = data.getDate().toString().padStart(2, '0');
    const ora = data.getHours().toString().padStart(2, '0');
    const minuti = data.getMinutes().toString().padStart(2, '0');
    return `${giorno} ${mese} ${ora}:${minuti}`;
  }
}
