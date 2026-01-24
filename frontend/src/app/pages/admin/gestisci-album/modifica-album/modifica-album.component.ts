import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlbumService } from '../../../../service/album.service';
import { FotoService } from '../../../../service/foto.service';
import { Album } from '../../../../models/album.model';
import { Foto } from '../../../../models/foto.model';

@Component({
  selector: 'app-modifica-album',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './modifica-album.component.html',
  styleUrls: ['./modifica-album.component.css']
})
export class ModificaAlbumComponent implements OnInit {
  albumId!: number;
  nome = '';
  descrizione = '';
  fotoCopertina: string | null = null;
  nuovaCopertina: File | null = null;
  copertinaPreview: string | null = null;

  fotoEsistenti: Foto[] = [];
  nuoveFoto: File[] = [];
  nuoveFotoPreview: string[] = [];

  loading = false;
  loadingFoto = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private albumService: AlbumService,
    private fotoService: FotoService
  ) {}

  ngOnInit() {
    this.albumId = Number(this.route.snapshot.paramMap.get('id'));
    this.caricaAlbum();
    this.caricaFoto();
  }

  caricaAlbum() {
    this.loading = true;
    console.log('🔄 Caricamento album:', this.albumId);

    this.albumService.getAlbumById(this.albumId).subscribe({
      next: (album) => {
        console.log('✅ Album caricato:', album);
        this.nome = album.nome;
        this.descrizione = album.descrizione || '';
        this.fotoCopertina = album.fotoCopertina || null;
        this.copertinaPreview = album.fotoCopertina || null;
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento album:', error);
        alert('Errore nel caricamento dell\'album');
        this.router.navigate(['/albums']);
      }
    });
  }

  caricaFoto() {
    this.loadingFoto = true;
    console.log('🔄 Caricamento foto album:', this.albumId);

    this.fotoService.getFotoByAlbumId(this.albumId).subscribe({
      next: (foto) => {
        console.log('✅ Foto caricate:', foto);
        this.fotoEsistenti = foto;
        this.loadingFoto = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento foto:', error);
        this.fotoEsistenti = [];
        this.loadingFoto = false;
      }
    });
  }

  onNuovaCopertinaSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.nuovaCopertina = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.copertinaPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  rimuoviNuovaCopertina() {
    this.nuovaCopertina = null;
    this.copertinaPreview = this.fotoCopertina;
  }

  onNuoveFotoSelected(event: any) {
    const files = Array.from(event.target.files) as File[];
    this.nuoveFoto = [...this.nuoveFoto, ...files];

    files.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.nuoveFotoPreview.push(e.target.result);
      };
      reader.readAsDataURL(file);
    });
  }

  rimuoviNuovaFoto(index: number) {
    this.nuoveFoto.splice(index, 1);
    this.nuoveFotoPreview.splice(index, 1);
  }

  eliminaFotoEsistente(foto: Foto) {
    const conferma = confirm('Sei sicuro di voler eliminare questa foto?');
    if (!conferma) return;

    console.log('🗑️ Eliminazione foto:', foto.idFoto);

    this.fotoService.deleteFoto(foto.idFoto!).subscribe({
      next: (response) => {
        console.log('✅ Foto eliminata:', response);
        this.fotoEsistenti = this.fotoEsistenti.filter(f => f.idFoto !== foto.idFoto);
        alert('✅ Foto eliminata con successo!');
      },
      error: (error) => {
        console.error('❌ Errore eliminazione foto:', error);
        alert('❌ Errore nell\'eliminazione della foto');
      }
    });
  }

  aggiornaAlbum() {
    if (!this.nome.trim()) {
      alert('❌ Inserisci un nome per l\'album');
      return;
    }

    this.loading = true;
    console.log('💾 Aggiornamento album:', this.albumId);

    const formData = new FormData();
    formData.append('nome', this.nome);
    formData.append('descrizione', this.descrizione);

    if (this.nuovaCopertina) {
      formData.append('copertina', this.nuovaCopertina);
    }

    this.albumService.updateAlbum(this.albumId, formData).subscribe({
      next: (response) => {
        console.log('✅ Album aggiornato:', response);

        // Se ci sono nuove foto da aggiungere
        if (this.nuoveFoto.length > 0) {
          this.caricaNuoveFoto();
        } else {
          alert(`✅ Album "${this.nome}" aggiornato con successo!`);
          this.router.navigate(['/albums']);
        }
      },
      error: (error) => {
        console.error('❌ Errore aggiornamento album:', error);
        alert('❌ Errore: ' + (error.error?.error || 'Servizio non disponibile'));
        this.loading = false;
      }
    });
  }

  caricaNuoveFoto() {
    console.log('📸 Caricamento nuove foto:', this.nuoveFoto.length);

    const formData = new FormData();
    this.nuoveFoto.forEach(foto => {
      formData.append('foto', foto);
    });
    formData.append('idAlbum', this.albumId.toString());

    this.fotoService.uploadFoto(formData).subscribe({
      next: (response) => {
        console.log('✅ Foto caricate:', response);
        alert(`✅ Album "${this.nome}" aggiornato con successo!\n${this.nuoveFoto.length} nuove foto aggiunte!`);
        this.router.navigate(['/albums']);
      },
      error: (error) => {
        console.error('❌ Errore caricamento foto:', error);
        alert('⚠️ Album aggiornato ma errore nel caricamento delle nuove foto');
        this.router.navigate(['/albums']);
      }
    });
  }

  annulla() {
    if (confirm('Sei sicuro di voler annullare? Tutte le modifiche andranno perse.')) {
      this.router.navigate(['/albums']);
    }
  }
}
