import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FeaturesComponent } from '../../components/features/features.component';
import { AlbumService } from '../../service/album.service';
import { Album } from '../../models/album.model';

@Component({
  selector: 'app-albums',
  standalone: true,
  imports: [CommonModule, RouterLink, FeaturesComponent],
  templateUrl: './album.component.html',
  styleUrls: ['./album.component.css']
})
export class AlbumsComponent implements OnInit {
  fullscreenMenu: boolean = false;
  albums: Album[] = [];
  loading = false;

  constructor(private albumService: AlbumService) {}

  ngOnInit(): void {
    this.caricaAlbums();
  }

  caricaAlbums() {
    this.loading = true;
    console.log('📸 Caricamento albums pubblici...');

    this.albumService.getAllAlbums().subscribe({
      next: (data) => {
        console.log('✅ Albums caricati:', data);
        this.albums = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('❌ Errore caricamento albums:', error);
        this.albums = [];
        this.loading = false;
      }
    });
  }
}
