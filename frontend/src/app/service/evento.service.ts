import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Evento } from '../models/evento.model';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private apiUrl = 'http://localhost:8080/api/eventi';
  private iscrizioniUrl = 'http://localhost:8080/api/iscrizioni';

  constructor(private http: HttpClient) {}

  // ===== METODI UTENTE =====
  getEventiProssimi(): Observable<Evento[]> {
    return this.http.get<Evento[]>(this.apiUrl);
  }

  getEventoById(id: number): Observable<Evento> {
    return this.http.get<Evento>(`${this.apiUrl}/${id}`);
  }

  iscrivitiEvento(iscrizione: any): Observable<any> {
    return this.http.post(this.iscrizioniUrl, iscrizione);
  }

  iscrizioneRapida(eventoId: number): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.post(`${this.iscrizioniUrl}/rapida`, { eventoId }, { headers });
  }

  verificaIscrizioneConsentita(eventoId: number): Observable<{ consentita: boolean }> {
    return this.http.get<{ consentita: boolean }>(
        `${this.iscrizioniUrl}/evento/${eventoId}/consentita`
    );
  }

  verificaIscrizioneUtente(eventoId: number): Observable<{ iscritto: boolean }> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
    return this.http.get<{ iscritto: boolean }>(
        `${this.iscrizioniUrl}/evento/${eventoId}/iscritto`,
        { headers }
    );
  }

  // ===== METODI ADMIN =====

  creaEvento(evento: Evento): Observable<any> {
    const headers = this.authHeaders();
    return this.http.post(this.apiUrl, evento, { headers });
  }

  aggiornaEvento(id: number, evento: Evento): Observable<any> {
    const headers = this.authHeaders();
    return this.http.put(`${this.apiUrl}/${id}`, evento, { headers });
  }

  // CREATE + UPLOAD FILE (FormData → /upload)
  uploadEvento(formData: FormData): Observable<any> {
    const token = localStorage.getItem('token');
    const headers = token
        ? new HttpHeaders({ 'Authorization': `Bearer ${token}` })
        : undefined;

    return this.http.post(`${this.apiUrl}/upload`, formData, { headers });
  }

  eliminaEvento(id: number): Observable<any> {
    const headers = this.authHeaders(false);
    return this.http.delete(`${this.apiUrl}/${id}`, { headers });
  }

  private authHeaders(withJson: boolean = true): HttpHeaders {
    const token = localStorage.getItem('token');
    let headers = new HttpHeaders();
    if (withJson) {
      headers = headers.set('Content-Type', 'application/json');
    }
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return headers;
  }
}
