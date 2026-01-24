import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProfiloService {
  private baseUrl = 'http://localhost:8080/api/profilo';

  constructor(private http: HttpClient) {}

  getDatiUtente(username: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${username}`);
  }

  aggiornaDati(username: string, dati: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${username}`, dati);
  }

  cambiaPassword(username: string, vecchiaPassword: string, nuovaPassword: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/${username}/cambiaPassword`, {
      vecchiaPassword,
      nuovaPassword
    });
  }
}
