// DTO Proxy (per la gestione utenti - ridotto)
export interface UtenteBanditoProxy {
  id: number;
  username: string;
  nome: string;
  cognome: string;
}

// DTO Completo (per admin o dettagli)
export interface UtenteBandito {
  id: number;
  username: string;
  nome: string;
  cognome: string;
  telefono: string;
  email: string;
  ruolo: number;
}
