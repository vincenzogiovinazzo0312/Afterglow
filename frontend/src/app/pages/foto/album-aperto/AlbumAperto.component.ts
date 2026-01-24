import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import { FeaturesComponent } from '../../../components/features/features.component';
import { AlbumService } from '../../../service/album.service';
import { FotoService } from '../../../service/foto.service';
import { LikeService, LikeDTO } from '../../../service/like.service';
import { CommentoService, CommentoDTO } from '../../../service/commento.service';
import { AuthService } from '../../../service/auth.service';

interface GalleryImage {
  id: number;
  src: string;
  alt?: string;
  comments: CommentoDTO[];
  likeCount: number;
  userLiked: boolean;
}

@Component({
  selector: 'app-AlbumAperto',
  standalone: true,
  imports: [CommonModule, FeaturesComponent, FormsModule, DatePipe, RouterLink],
  templateUrl: './AlbumAperto.component.html',
  styleUrls: ['./AlbumAperto.component.css']
})
export class AlbumApertoComponent implements OnInit {

  albumId!: number;
  loggedUsername: string | null = null;  // ✅ Può essere null
  isLoggedIn: boolean = false;           // ✅ Flag per controllo login
  albumTitle = '';
  albumDescription = '';
  images: GalleryImage[] = [];
  loading = false;

  lightboxOpen = false;
  currentIndex = 0;
  newComment: string = "";
  mobileCommentsOpen = false;

  constructor(
      private route: ActivatedRoute,
      private router: Router,
      private albumService: AlbumService,
      private fotoService: FotoService,
      private likeService: LikeService,
      private commentoService: CommentoService,
      private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.albumId = Number(this.route.snapshot.paramMap.get('id'));

    // ✅ Controlla se l'utente è loggato (ma NON blocca l'accesso)
    this.isLoggedIn = this.authService.isLoggedIn();
    this.loggedUsername = this.authService.getUsername();

    if (this.isLoggedIn && this.loggedUsername) {
      console.log('👤 Utente loggato:', this.loggedUsername);
    } else {
      console.log('👁️ Utente NON loggato - Modalità visualizzazione');
    }

    this.caricaAlbum();
    this.caricaFoto();
  }

  caricaAlbum() {
    this.albumService.getAlbumById(this.albumId).subscribe({
      next: (album) => {
        this.albumTitle = album.nome;
        this.albumDescription = album.descrizione || 'Nessuna descrizione';
      },
      error: (error) => {
        console.error('❌ Errore caricamento album:', error);
      }
    });
  }

  caricaFoto() {
    this.loading = true;
    console.log('📸 Caricamento foto album:', this.albumId);

    this.fotoService.getFotoByAlbumId(this.albumId).subscribe({
      next: (foto) => {
        console.log('✅ Foto caricate:', foto);

        this.images = foto
            .filter(f => f.idFoto !== null && f.idFoto !== undefined)
            .map((f, index) => ({
              id: f.idFoto as number,
              src: f.percorso || '',
              alt: `Foto ${index + 1}`,
              comments: [],
              likeCount: 0,
              userLiked: false
            }));

        // ✅ Carica like e commenti SOLO se l'utente è loggato
        if (this.isLoggedIn && this.loggedUsername) {
          this.images.forEach(img => {
            this.loadLikeInfo(img);
            this.loadComments(img);
          });
        } else {
          console.log('ℹ️ Like e commenti disabilitati (utente non loggato)');
        }

        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento foto:', error);
        this.images = [];
        this.loading = false;
      }
    });
  }

  // Carica info like dal DB (SOLO se loggato)
  loadLikeInfo(img: GalleryImage) {
    if (!this.loggedUsername) return;

    this.likeService.getLikeInfo(img.id, this.loggedUsername).subscribe({
      next: (likeInfo: LikeDTO) => {
        img.likeCount = likeInfo.count;
        img.userLiked = likeInfo.liked;
        console.log(`✅ Like foto ${img.id}: ${likeInfo.count} (user liked: ${likeInfo.liked})`);
      },
      error: (error) => {
        console.error('❌ Errore caricamento like:', error);
      }
    });
  }

  // Carica commenti dal DB (SOLO se loggato)
  loadComments(img: GalleryImage) {
    this.commentoService.getCommenti(img.id).subscribe({
      next: (commenti: CommentoDTO[]) => {
        img.comments = commenti;
        console.log(`✅ Commenti foto ${img.id}:`, commenti.length);
      },
      error: (error) => {
        console.error('❌ Errore caricamento commenti:', error);
      }
    });
  }

  tornaAllaLista() {
    this.router.navigate(['/foto']);
  }

  get currentImage() {
    if (!this.images.length) return null;
    return this.images[this.currentIndex];
  }

  openLightbox(index: number): void {
    this.currentIndex = index;
    this.lightboxOpen = true;
    document.body.style.overflow = "hidden";
  }

  closeLightbox(): void {
    this.lightboxOpen = false;
    document.body.style.overflow = "auto";
    this.mobileCommentsOpen = false;
  }

  nextImage(): void {
    this.currentIndex = (this.currentIndex + 1) % this.images.length;
  }

  prevImage(): void {
    this.currentIndex = (this.currentIndex - 1 + this.images.length) % this.images.length;
  }

  openMobileComments(): void {
    // ✅ Blocca se non loggato
    if (!this.isLoggedIn) {
      alert('Effettua il login per visualizzare i commenti');
      return;
    }
    this.mobileCommentsOpen = true;
  }

  closeMobileComments(): void {
    this.mobileCommentsOpen = false;
  }

  // ========== LIKE ==========

  toggleLike(): void {
    // ✅ Blocca se non loggato
    if (!this.isLoggedIn || !this.loggedUsername) {
      alert('Effettua il login per mettere like');
      return;
    }

    const img = this.currentImage;
    if (!img) return;

    console.log('🔄 Toggle like - Foto ID:', img.id, 'Username:', this.loggedUsername);

    this.likeService.toggleLike(img.id, this.loggedUsername).subscribe({
      next: (result: LikeDTO) => {
        img.userLiked = result.liked;
        img.likeCount = result.count;
        console.log('✅ Like aggiornato:', result);
      },
      error: (error) => {
        console.error('❌ Errore toggle like:', error);
        alert('Errore durante l\'aggiornamento del like');
      }
    });
  }

  hasLiked(): boolean {
    const img = this.currentImage;
    return img ? img.userLiked : false;
  }

  // ========== COMMENTI ==========

  addComment(): void {
    // ✅ Blocca se non loggato
    if (!this.isLoggedIn || !this.loggedUsername) {
      alert('Effettua il login per commentare');
      return;
    }

    const img = this.currentImage;
    if (!img) return;
    if (!this.newComment.trim()) return;

    console.log('💬 Aggiunta commento - Foto ID:', img.id, 'Username:', this.loggedUsername, 'Testo:', this.newComment);

    this.commentoService.addCommento(img.id, this.loggedUsername, this.newComment).subscribe({
      next: () => {
        console.log('✅ Commento aggiunto');
        this.loadComments(img);
        this.newComment = "";
      },
      error: (error) => {
        console.error('❌ Errore aggiunta commento:', error);
        alert('Errore durante l\'aggiunta del commento');
      }
    });
  }

  deleteComment(idCommento: number): void {
    if (!confirm('Sei sicuro di voler eliminare questo commento?')) return;

    console.log('🗑️ Eliminazione commento ID:', idCommento);

    this.commentoService.deleteCommento(idCommento).subscribe({
      next: () => {
        console.log('✅ Commento eliminato');
        const img = this.currentImage;
        if (img) {
          this.loadComments(img);
        }
      },
      error: (error) => {
        console.error('❌ Errore eliminazione commento:', error);
        alert('Errore durante l\'eliminazione del commento');
      }
    });
  }

  // ========== DOWNLOAD ==========

  downloadImage(): void {
    const img = this.currentImage;
    if (!img?.src) return;

    console.log('📥 Avvio download foto:', img.src);

    fetch(img.src)
        .then(response => response.blob())
        .then(blob => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `foto_${this.currentIndex + 1}.jpg`;

          document.body.appendChild(link);
          link.click();

          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);

          console.log('✅ Download completato');
        })
        .catch(error => {
          console.error('❌ Errore download:', error);
          alert('Errore durante il download della foto');
        });
  }
}
