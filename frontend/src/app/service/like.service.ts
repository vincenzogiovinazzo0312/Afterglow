import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LikeDTO {
  idFoto: number;
  username: string;  // ✅ Cambiato da idUtente
  liked: boolean;
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class LikeService {
  private apiUrl = 'http://localhost:8080/api/foto';

  constructor(private http: HttpClient) {}

  // Ottieni info like usando username
  getLikeInfo(idFoto: number, username: string): Observable<LikeDTO> {
    const params = new HttpParams().set('username', username);
    return this.http.get<LikeDTO>(`${this.apiUrl}/${idFoto}/likes`, { params });
  }

  // Toggle like usando username
  toggleLike(idFoto: number, username: string): Observable<LikeDTO> {
    const params = new HttpParams().set('username', username);
    return this.http.post<LikeDTO>(`${this.apiUrl}/${idFoto}/likes/toggle`, {}, { params });
  }
}
