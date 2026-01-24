import { Component, OnInit, inject, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-gestione-liste',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './gestione-liste.component.html',
  styleUrl: './gestione-liste.component.css',
})
export class GestioneListeComponent implements OnInit {
  private http = inject(HttpClient);
  listaEventi: any[] = [];

  // Carosello
  currentIndex = 0;
  itemsPerPage = 3;
  slideWidth = 33.333;

  ngOnInit() {
    this.http.get('http://localhost:8080/api/eventi')
      .subscribe((datiRicevuti: any) => {
        this.listaEventi = datiRicevuti;
        console.log("Eventi caricati:", this.listaEventi);
        this.updateCarousel();
      });

    // Inizializza carosello
    this.updateCarousel();
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.updateCarousel();
  }

  updateCarousel() {
    const width = window.innerWidth;

    if (width <= 768) {
      this.itemsPerPage = 1;
      this.slideWidth = 100;
    } else if (width <= 1024) {
      this.itemsPerPage = 2;
      this.slideWidth = 50;
    } else {
      this.itemsPerPage = 3;
      this.slideWidth = 33.333;
    }

    // Reset index se supera il limite
    const maxIndex = Math.max(0, this.listaEventi.length - this.itemsPerPage);
    if (this.currentIndex > maxIndex) {
      this.currentIndex = maxIndex;
    }
  }

  nextSlide() {
    const maxIndex = this.listaEventi.length - this.itemsPerPage;
    if (this.currentIndex < maxIndex) {
      this.currentIndex++;
    } else {
      this.currentIndex = 0;
    }
  }

  prevSlide() {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    } else {
      this.currentIndex = Math.max(0, this.listaEventi.length - this.itemsPerPage);
    }
  }

  menuAperto = false;
  toggleMenu() {
    this.menuAperto = !this.menuAperto;
  }
}
