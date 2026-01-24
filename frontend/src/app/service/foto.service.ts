import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Foto } from '../models/foto.model';

@Injectable({
  providedIn: 'root'
})
export class FotoService {
  private apiUrl = '/api/foto';

  constructor(private http: HttpClient) {}

  getAllFoto(): Observable<Foto[]> {
    return this.http.get<Foto[]>(this.apiUrl);
  }

  getFotoById(id: number): Observable<Foto> {
    return this.http.get<Foto>(`${this.apiUrl}/${id}`);
  }

  getFotoByAlbumId(albumId: number): Observable<Foto[]> {
    return this.http.get<Foto[]>(`${this.apiUrl}/album/${albumId}`);
  }

  uploadFoto(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/upload`, formData);
  }

  deleteFoto(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
