import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { IscrizioneEventoService } from '../../../../service/iscrizione-evento.service';

@Component({
  selector: 'app-dettaglio-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './dettaglio-lista.component.html',
  styleUrl: './dettaglio-lista.component.css'
})
export class DettaglioListaComponent implements OnInit {

  eventoId: number = 0;
  nomeEvento: string = '';
  searchNome: string = '';
  searchCognome: string = '';
  listaCompleta: any[] = [];
  listaVisualizzata: any[] = [];

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private iscrizioneService: IscrizioneEventoService
  ) {
    const idParam = this.route.snapshot.paramMap.get('id');
    this.eventoId = idParam ? Number(idParam) : 0;
  }

  ngOnInit() {
    this.caricaEvento();
    this.caricaIscrizioni();
  }

  caricaEvento() {
    if (!this.eventoId) { return; }
    this.http.get<any>(`http://localhost:8080/api/eventi/${this.eventoId}`)
      .subscribe({
        next: evento => {
          this.nomeEvento = evento.titolo || 'Evento';
        },
        error: err => console.error('Errore caricamento evento', err)
      });
  }

  caricaIscrizioni() {
    if (!this.eventoId) { return; }
    this.iscrizioneService.getIscrizioniByEvento(this.eventoId)
      .subscribe({
        next: dati => {
          this.listaCompleta = dati;
          this.listaVisualizzata = [...this.listaCompleta];
        },
        error: err => console.error('Errore caricamento iscrizioni', err)
      });
  }

  cerca() {
    this.listaVisualizzata = this.listaCompleta.filter(p => {
      const matchNome = p.nome?.toLowerCase().includes(this.searchNome.toLowerCase());
      const matchCognome = p.cognome?.toLowerCase().includes(this.searchCognome.toLowerCase());
      return matchNome && matchCognome;
    });
  }

  reset() {
    this.searchNome = '';
    this.searchCognome = '';
    this.listaVisualizzata = [...this.listaCompleta];
  }

  toggleEntrato(persona: any) {
    const nuovoStato = !persona.entrato;
    this.iscrizioneService.aggiornaStatoEntrato(persona.id, nuovoStato)
      .subscribe({
        next: () => {
          persona.entrato = nuovoStato;
        },
        error: err => console.error('Errore aggiornamento stato', err)
      });
  }

  esportaPDF() {
    const doc = new jsPDF();
    doc.text('Lista Partecipanti - ' + this.nomeEvento, 14, 20);
    const datiTabella = this.listaVisualizzata.map(p => [
      p.cognome,
      p.nome,
      p.telefono,
      p.entrato ? 'ENTRATO' : 'IN ATTESA'
    ]);
    autoTable(doc, {
      head: [['Cognome', 'Nome', 'Telefono', 'Stato']],
      body: datiTabella,
      startY: 30,
      theme: 'grid',
      styles: { fontSize: 10 },
      headStyles: { fillColor: [248, 192, 62] }
    });
    doc.save('lista_partecipanti.pdf');
  }
}
