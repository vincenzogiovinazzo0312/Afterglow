import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UtenteBanditoProxy, UtenteBandito } from '../models/utente.model';

@Injectable({
  providedIn: 'root'
})
export class UtenteBanditoService {
  private apiUrl = '/api/utenti';

  constructor(private http: HttpClient) {}

  // ============= METODI CON PROXY DTO (ridotto) =============

  getUtentiNonBanditi(): Observable<UtenteBanditoProxy[]> {
    return this.http.get<UtenteBanditoProxy[]>(`${this.apiUrl}/non-banditi`);
  }

  getUtentiBanditi(): Observable<UtenteBanditoProxy[]> {
    return this.http.get<UtenteBanditoProxy[]>(`${this.apiUrl}/banditi`);
  }

  cercaUtenti(nome: string, cognome: string): Observable<UtenteBanditoProxy[]> {
    return this.http.get<UtenteBanditoProxy[]>(
      `${this.apiUrl}/cerca?nome=${nome}&cognome=${cognome}`
    );
  }

  // ============= METODI CON DTO COMPLETO =============

  getUtentiNonBanditiCompleto(): Observable<UtenteBandito[]> {
    return this.http.get<UtenteBandito[]>(`${this.apiUrl}/non-banditi/completo`);
  }

  getUtentiBanditiCompleto(): Observable<UtenteBandito[]> {
    return this.http.get<UtenteBandito[]>(`${this.apiUrl}/banditi/completo`);
  }

  getUtenteCompleto(id: number): Observable<UtenteBandito> {
    return this.http.get<UtenteBandito>(`${this.apiUrl}/${id}/completo`);
  }

  // ============= OPERAZIONI BANDISCI/RIPRISTINA =============

  bandisciUtente(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/bandisci/${id}`, {});
  }

  ripristinaUtente(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/ripristina/${id}`, {});
  }
}
