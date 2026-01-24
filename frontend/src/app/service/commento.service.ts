import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CommentoDTO {
  idCommento: number;
  idFoto: number;
  username: string;
  nomeUtente: string;
  testo: string;
  dataCommento: string;
}

@Injectable({
  providedIn: 'root'
})
export class CommentoService {
  private apiUrl = 'http://localhost:8080/api/foto';

  constructor(private http: HttpClient) {}

  getCommenti(idFoto: number): Observable<CommentoDTO[]> {
    return this.http.get<CommentoDTO[]>(`${this.apiUrl}/${idFoto}/commenti`);
  }

  addCommento(idFoto: number, username: string, testo: string): Observable<any> {
    const params = new HttpParams()
      .set('username', username)
      .set('testo', testo);

    return this.http.post(`${this.apiUrl}/${idFoto}/commenti`, {}, { params });
  }

  deleteCommento(idCommento: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/commenti/${idCommento}`);
  }

  getCommentCount(idFoto: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.apiUrl}/${idFoto}/commenti/count`);
  }
}
