import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AlbumService } from '../../../../service/album.service';
import { Album } from '../../../../models/album.model';

@Component({
  selector: 'app-lista-albums',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './lista-album.component.html',
  styleUrls: ['./lista-album.component.css']
})
export class ListaAlbumsComponent implements OnInit {
  albums: Album[] = [];
  albumsFiltrati: Album[] = [];
  loading = false;
  searchTerm = '';

  // Paginazione
  currentPage = 1;
  itemsPerPage = 6;
  totalPages = 1;
  albumsPaginati: Album[] = [];

  constructor(
    private albumService: AlbumService,
    private router: Router
  ) {}

  ngOnInit() {
    this.caricaAlbums();
  }

  caricaAlbums() {
    this.loading = true;
    console.log('🔄 Caricamento albums...');

    this.albumService.getAllAlbums().subscribe({
      next: (data) => {
        console.log('✅ Albums caricati:', data);
        this.albums = data;
        this.albumsFiltrati = data;
        this.currentPage = 1;
        this.updatePagination();
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento albums:', error);
        alert('Errore nel caricamento degli album');
        this.loading = false;
      }
    });
  }

  cerca() {
    if (!this.searchTerm.trim()) {
      this.albumsFiltrati = this.albums;
    } else {
      this.albumService.searchAlbums(this.searchTerm).subscribe({
        next: (data) => {
          console.log('🔍 Risultati ricerca:', data);
          this.albumsFiltrati = data;
          this.currentPage = 1;
          this.updatePagination();
        },
        error: (error) => {
          console.error('❌ Errore ricerca:', error);
          this.albumsFiltrati = [];
          this.updatePagination();
        }
      });
    }
  }

  resetRicerca() {
    this.searchTerm = '';
    this.albumsFiltrati = this.albums;
    this.currentPage = 1;
    this.updatePagination();
  }

  // Paginazione
  updatePagination() {
    this.totalPages = Math.ceil(this.albumsFiltrati.length / this.itemsPerPage);
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.albumsPaginati = this.albumsFiltrati.slice(startIndex, endIndex);
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.updatePagination();
    }
  }

  previousPage() {
    this.goToPage(this.currentPage - 1);
  }

  nextPage() {
    this.goToPage(this.currentPage + 1);
  }

  getPagesArray(): number[] {
    return Array.from({ length: this.totalPages }, (_, i) => i + 1);
  }

  // Azioni
  creaAlbum() {
    this.router.navigate(['/admin/gestisci-foto']);
  }

  modificaAlbum(id: number) {
    this.router.navigate(['/admin/modifica-album', id]);
  }


  visualizzaAlbum(id: number) {
    this.router.navigate(['/admin/album', id]);
  }

  eliminaAlbum(album: Album) {
    const conferma = confirm(`Sei sicuro di voler eliminare l'album "${album.nome}"?\nVerranno eliminate anche tutte le foto (${album.numeroFoto || 0} foto).`);
    if (!conferma) return;

    this.loading = true;
    console.log('🗑️ Eliminazione album:', album.id);

    this.albumService.deleteAlbum(album.id!).subscribe({
      next: (response) => {
        console.log('✅ Album eliminato:', response);
        alert(`✅ Album "${album.nome}" eliminato con successo!`);
        this.caricaAlbums();
      },
      error: (error) => {
        console.error('❌ Errore eliminazione:', error);
        alert('❌ Errore: ' + (error.error?.error || 'Servizio non disponibile'));
        this.loading = false;
      }
    });
  }
}
