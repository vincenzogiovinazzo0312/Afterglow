import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AlbumService } from '../../../../service/album.service';

@Component({
  selector: 'app-crea-album',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crea-album.component.html',
  styleUrls: ['./crea-album.component.css']
})
export class CreaAlbumComponent {
  nome = '';
  descrizione = '';
  copertina: File | null = null;
  fotoSelezionate: File[] = [];
  previews: string[] = [];
  copertinaPreview: string | null = null;
  loading = false;

  constructor(
    private albumService: AlbumService,
    private router: Router
  ) {}

  onCopertinaSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.copertina = file;
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.copertinaPreview = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onFotoSelected(event: any) {
    const files = Array.from(event.target.files) as File[];
    this.fotoSelezionate = [...this.fotoSelezionate, ...files];

    files.forEach(file => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.previews.push(e.target.result);
      };
      reader.readAsDataURL(file);
    });
  }

  rimuoviFoto(index: number) {
    this.fotoSelezionate.splice(index, 1);
    this.previews.splice(index, 1);
  }

  rimuoviCopertina() {
    this.copertina = null;
    this.copertinaPreview = null;
  }

  creaAlbum() {
    if (!this.nome.trim()) {
      alert('❌ Inserisci un nome per l\'album');
      return;
    }

    this.loading = true;
    console.log('📸 Creazione album:', this.nome);

    const formData = new FormData();
    formData.append('nome', this.nome);
    formData.append('descrizione', this.descrizione);

    if (this.copertina) {
      formData.append('copertina', this.copertina);
    }

    this.fotoSelezionate.forEach(foto => {
      formData.append('foto', foto);
    });

    this.albumService.createAlbum(formData).subscribe({
      next: (response) => {
        console.log('✅ Album creato:', response);

        // Messaggio di successo
        const messaggio = response.cloudinary
          ? `✅ Album "${this.nome}" creato con successo su Cloudinary!\n📁 Cartella: ${response.folder}\n📸 Foto caricate: ${response.numeroFoto}`
          : `✅ Album "${this.nome}" creato con successo!\n📁 Cartella: ${response.folder}\n📸 Foto caricate: ${response.numeroFoto}`;

        alert(messaggio);

        // ✅ REDIRECT alla lista album
        this.router.navigate(['/admin/lista-album']);
      },
      error: (error) => {
        console.error('❌ Errore creazione album:', error);
        alert('❌ Errore: ' + (error.error?.error || 'Servizio non disponibile'));
        this.loading = false;
      }
    });
  }

  annulla() {
    if (confirm('Sei sicuro di voler annullare? Tutti i dati inseriti verranno persi.')) {
      this.router.navigate(['/admin/lista-album']);  // ✅ REDIRECT
    }
  }
}
