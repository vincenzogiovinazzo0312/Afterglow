import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IscrizioneEventoService {

  private baseUrl = 'http://localhost:8080/api/iscrizioni';

  constructor(private http: HttpClient) {}

  getIscrizioniByEvento(eventoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/evento/${eventoId}`);
  }

  contaIscrizioni(eventoId: number): Observable<{ count: number }> {
    return this.http.get<{ count: number }>(`${this.baseUrl}/evento/${eventoId}/count`);
  }

  creaIscrizione(dto: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, dto);
  }

  creaIscrizioneRapida(dto: any, token: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/rapida`, dto, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  verificaIscrizioneConsentita(eventoId: number): Observable<{ consentita: boolean }> {
    return this.http.get<{ consentita: boolean }>(
      `${this.baseUrl}/evento/${eventoId}/consentita`
    );
  }

  verificaUtenteIscritto(eventoId: number, token: string): Observable<{ iscritto: boolean }> {
    return this.http.get<{ iscritto: boolean }>(
      `${this.baseUrl}/evento/${eventoId}/iscritto`,
      { headers: { Authorization: `Bearer ${token}` } }
    );
  }

  deleteIscrizione(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  aggiornaStatoEntrato(id: number, entrato: boolean): Observable<void> {
    return this.http.put<void>(
      `${this.baseUrl}/${id}/stato`,
      null,
      { params: { entrato } }
    );
  }
}
