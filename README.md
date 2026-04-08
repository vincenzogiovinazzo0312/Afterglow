Afterglow è una piattaforma web completa progettata per modernizzare l'esperienza delle discoteche e dei club. Il sistema offre una soluzione end-to-end: dalla promozione degli eventi tramite gallerie fotografiche e chatbot basati su AI, alla gestione amministrativa delle liste d'ingresso e degli utenti.

🚀 Caratteristiche Principali
Esperienza Utente (Frontend)
Dashboard Eventi: Visualizzazione dinamica degli eventi in programma con dettagli su data, ora e tipologia.

Iscrizione Rapida: Sistema semplificato per l'iscrizione alle liste degli eventi direttamente dal portale.

Galleria Multimediale: Navigazione degli album fotografici degli eventi passati con funzionalità di "Like" e commenti.

Chatbot AI: Assistente virtuale integrato (tramite Groq API) per rispondere alle domande frequenti degli utenti.

Area Profilo: Gestione dei dati personali, storico iscrizioni e recupero password tramite OTP.

Gestione Amministrativa (Admin Panel)
Controllo Utenti: Monitoraggio della base utenti con possibilità di sospensione o ban per comportamenti non idonei.

Content Management: Creazione, modifica ed eliminazione di eventi e album fotografici in tempo reale.

Analisi Liste: Gestione dei partecipanti iscritti per ogni singolo evento.

🛠️ Stack Tecnologico
Backend (Java/Spring Boot)
Framework: Spring Boot 4.0.0.

Sicurezza: Spring Security con integrazione JWT (JSON Web Token) per sessioni stateless e sicure.

Database: PostgreSQL gestito tramite Spring Data JPA.

Integrazioni Terze:

Cloudinary: Per l'hosting e la trasformazione ottimizzata delle immagini.

Groq API: Per alimentare il motore di intelligenza artificiale della chat.

Google OAuth: Supporto per il login tramite account Google.

Frontend (Angular)
Framework: Angular 20.3.9.

Styling: CSS3 moderno con approccio Responsive Design.

Comunicazione: Client HTTP con interceptor per la gestione automatica dei token JWT.
