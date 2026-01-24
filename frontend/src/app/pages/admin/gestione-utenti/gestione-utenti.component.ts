import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { UtenteBanditoService } from '../../../service/utente.service';
import { UtenteBanditoProxy } from '../../../models/utente.model';

@Component({
  selector: 'app-gestione-utenti',
  standalone: true,
  imports: [CommonModule, RouterModule, HttpClientModule, FormsModule],
  templateUrl: './gestione-utenti.component.html',
  styleUrls: ['./gestione-utenti.component.css']
})
export class GestioneUtentiComponent implements OnInit {
  isMenuOpen = false;

  // Dati completi
  utentiNonBanditi: UtenteBanditoProxy[] = [];
  utentiBanditi: UtenteBanditoProxy[] = [];

  // Dati paginati (da visualizzare)
  utentiNonBanditiPaginati: UtenteBanditoProxy[] = [];
  utentiBanditiPaginati: UtenteBanditoProxy[] = [];

  loading = false;
  searchName = '';
  searchSurname = '';

  // Paginazione utenti non banditi
  currentPageNonBanditi = 1;
  itemsPerPageNonBanditi = 10; // numero di utenti per pagina
  totalPagesNonBanditi = 1;

  // Paginazione utenti banditi
  currentPageBanditi = 1;
  itemsPerPageBanditi = 10;
  totalPagesBanditi = 1;

  constructor(private utenteService: UtenteBanditoService) {}

  toggleMenu() {
    this.isMenuOpen = !this.isMenuOpen;
  }

  closeMenu() {
    this.isMenuOpen = false;
  }

  caricaListe() {
    this.loading = true;
    console.log('🔄 Caricamento utenti...');

    this.utenteService.getUtentiNonBanditi().subscribe({
      next: (data) => {
        console.log('✅ Utenti non banditi caricati:', data);
        this.utentiNonBanditi = data;
        this.currentPageNonBanditi = 1;
        this.updatePaginationNonBanditi();
      },
      error: (error) => {
        console.error('❌ Errore caricamento utenti non banditi:', error);
        this.utentiNonBanditi = [];
        this.utentiNonBanditiPaginati = [];
        alert('Errore nel caricamento degli utenti');
      }
    });

    this.utenteService.getUtentiBanditi().subscribe({
      next: (data) => {
        console.log('✅ Utenti banditi caricati:', data);
        this.utentiBanditi = data;
        this.currentPageBanditi = 1;
        this.updatePaginationBanditi();
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento utenti banditi:', error);
        this.utentiBanditi = [];
        this.utentiBanditiPaginati = [];
        this.loading = false;
      }
    });
  }

  // Aggiorna paginazione utenti non banditi
  updatePaginationNonBanditi() {
    this.totalPagesNonBanditi = Math.ceil(this.utentiNonBanditi.length / this.itemsPerPageNonBanditi);
    const startIndex = (this.currentPageNonBanditi - 1) * this.itemsPerPageNonBanditi;
    const endIndex = startIndex + this.itemsPerPageNonBanditi;
    this.utentiNonBanditiPaginati = this.utentiNonBanditi.slice(startIndex, endIndex);
  }

  // Aggiorna paginazione utenti banditi
  updatePaginationBanditi() {
    this.totalPagesBanditi = Math.ceil(this.utentiBanditi.length / this.itemsPerPageBanditi);
    const startIndex = (this.currentPageBanditi - 1) * this.itemsPerPageBanditi;
    const endIndex = startIndex + this.itemsPerPageBanditi;
    this.utentiBanditiPaginati = this.utentiBanditi.slice(startIndex, endIndex);
  }

  // Navigazione pagine non banditi
  goToPageNonBanditi(page: number) {
    if (page >= 1 && page <= this.totalPagesNonBanditi) {
      this.currentPageNonBanditi = page;
      this.updatePaginationNonBanditi();
    }
  }

  previousPageNonBanditi() {
    this.goToPageNonBanditi(this.currentPageNonBanditi - 1);
  }

  nextPageNonBanditi() {
    this.goToPageNonBanditi(this.currentPageNonBanditi + 1);
  }

  // Navigazione pagine banditi
  goToPageBanditi(page: number) {
    if (page >= 1 && page <= this.totalPagesBanditi) {
      this.currentPageBanditi = page;
      this.updatePaginationBanditi();
    }
  }

  previousPageBanditi() {
    this.goToPageBanditi(this.currentPageBanditi - 1);
  }

  nextPageBanditi() {
    this.goToPageBanditi(this.currentPageBanditi + 1);
  }

  // Array di numeri pagine da visualizzare
  getPagesArrayNonBanditi(): number[] {
    return Array.from({ length: this.totalPagesNonBanditi }, (_, i) => i + 1);
  }

  getPagesArrayBanditi(): number[] {
    return Array.from({ length: this.totalPagesBanditi }, (_, i) => i + 1);
  }

  cerca() {
    if (!this.searchName.trim() && !this.searchSurname.trim()) {
      this.caricaListe();
      return;
    }

    this.loading = true;
    console.log('🔍 Ricerca utenti:', this.searchName, this.searchSurname);

    this.utenteService.getUtentiNonBanditi().subscribe({
      next: (data) => {
        this.utentiNonBanditi = data.filter(u => {
          const matchNome = !this.searchName.trim() ||
            u.nome.toLowerCase().includes(this.searchName.trim().toLowerCase());
          const matchCognome = !this.searchSurname.trim() ||
            u.cognome.toLowerCase().includes(this.searchSurname.trim().toLowerCase());
          return matchNome && matchCognome;
        });
        this.currentPageNonBanditi = 1;
        this.updatePaginationNonBanditi();
      },
      error: (error) => {
        console.error('❌ Errore ricerca non banditi:', error);
        this.utentiNonBanditi = [];
        this.utentiNonBanditiPaginati = [];
      }
    });

    this.utenteService.getUtentiBanditi().subscribe({
      next: (data) => {
        this.utentiBanditi = data.filter(u => {
          const matchNome = !this.searchName.trim() ||
            u.nome.toLowerCase().includes(this.searchName.trim().toLowerCase());
          const matchCognome = !this.searchSurname.trim() ||
            u.cognome.toLowerCase().includes(this.searchSurname.trim().toLowerCase());
          return matchNome && matchCognome;
        });
        this.currentPageBanditi = 1;
        this.updatePaginationBanditi();
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore ricerca banditi:', error);
        this.utentiBanditi = [];
        this.utentiBanditiPaginati = [];
        this.loading = false;
      }
    });
  }

  resetRicerca() {
    this.searchName = '';
    this.searchSurname = '';
    this.caricaListe();
  }

  bandisci(id: number) {
    const utente = this.utentiNonBanditi.find(u => u.id === id);
    if (!utente) {
      console.error('❌ Utente non trovato con id:', id);
      return;
    }

    const conferma = confirm(`Sei sicuro di voler bandire ${utente.nome} ${utente.cognome}?`);
    if (!conferma) return;

    console.log('🚫 Bandisci utente:', id);
    this.loading = true;

    this.utenteService.bandisciUtente(id).subscribe({
      next: (resp) => {
        console.log('✅ Utente bandito:', resp);
        alert(`✅ ${utente.nome} ${utente.cognome} è stato bandito con successo!`);
        this.caricaListe();
      },
      error: (err) => {
        console.error('❌ Errore bandisci:', err);
        alert('❌ Errore: ' + (err.error?.error || 'Servizio non disponibile'));
        this.loading = false;
      }
    });
  }

  ripristina(id: number) {
    const utente = this.utentiBanditi.find(u => u.id === id);
    if (!utente) {
      console.error('❌ Utente bandito non trovato con id:', id);
      return;
    }

    const conferma = confirm(`Sei sicuro di voler ripristinare ${utente.nome} ${utente.cognome}?`);
    if (!conferma) return;

    console.log('✅ Ripristina utente:', id);
    this.loading = true;

    this.utenteService.ripristinaUtente(id).subscribe({
      next: (resp) => {
        console.log('✅ Utente ripristinato:', resp);
        alert(`✅ ${utente.nome} ${utente.cognome} è stato ripristinato con successo!`);
        this.caricaListe();
      },
      error: (err) => {
        console.error('❌ Errore ripristina:', err);
        alert('❌ Errore: ' + (err.error?.error || 'Servizio non disponibile'));
        this.loading = false;
      }
    });
  }

  ngOnInit() {
    this.caricaListe();
  }
}
