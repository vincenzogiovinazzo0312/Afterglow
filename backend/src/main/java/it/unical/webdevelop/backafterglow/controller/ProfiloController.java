package it.unical.webdevelop.backafterglow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

import it.unical.webdevelop.backafterglow.dao.UserDao;
import it.unical.webdevelop.backafterglow.dao.CredenzialiDao;
import it.unical.webdevelop.backafterglow.model.Utente;
import it.unical.webdevelop.backafterglow.model.Credenziali;
import it.unical.webdevelop.backafterglow.services.PasswordService;
import it.unical.webdevelop.backafterglow.services.PasswordValidator;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/profilo")
public class ProfiloController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CredenzialiDao credenzialiDao;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PasswordValidator passwordValidator;


    @GetMapping("/{username}")
    public ResponseEntity<?> getDatiUtente(@PathVariable String username) {
        try {
            Utente utente = userDao.findByUsername(username);

            if (utente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utente non trovato"));
            }

            // Restituisci solo i dati necessari (senza password)
            return ResponseEntity.ok(Map.of(
                    "username", utente.getUsername(),
                    "nome", utente.getNome(),
                    "cognome", utente.getCognome(),
                    "email", utente.getEmail(),
                    "telefono", utente.getTelefono() != null ? utente.getTelefono() : ""
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Errore nel recupero dei dati: " + ex.getMessage()));
        }
    }


    @PutMapping("/{username}")
    public ResponseEntity<?> aggiornaDati(
            @PathVariable String username,
            @RequestBody Map<String, String> datiAggiornati) {
        try {
            Utente utente = userDao.findByUsername(username);

            if (utente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utente non trovato"));
            }

            // Aggiorna i campi modificabili
            if (datiAggiornati.containsKey("nome")) {
                utente.setNome(datiAggiornati.get("nome"));
            }
            if (datiAggiornati.containsKey("cognome")) {
                utente.setCognome(datiAggiornati.get("cognome"));
            }
            if (datiAggiornati.containsKey("email")) {
                utente.setEmail(datiAggiornati.get("email"));
            }
            if (datiAggiornati.containsKey("telefono")) {
                utente.setTelefono(datiAggiornati.get("telefono"));
            }

            // Salva nel database (usa update che aggiorna in base all'ID)
            int rowsAffected = userDao.update(utente);

            if (rowsAffected > 0) {
                return ResponseEntity.ok(Map.of(
                        "message", "Dati aggiornati con successo",
                        "utente", Map.of(
                                "username", utente.getUsername(),
                                "nome", utente.getNome(),
                                "cognome", utente.getCognome(),
                                "email", utente.getEmail(),
                                "telefono", utente.getTelefono() != null ? utente.getTelefono() : ""
                        )
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Errore durante l'aggiornamento"));
            }

        } catch (DataIntegrityViolationException ex) {
            String msg = ex.getMostSpecificCause().getMessage();
            if (msg != null && msg.contains("utenti_email_key")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email già in uso da un altro utente"));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Dati duplicati"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Errore di sistema: " + ex.getMessage()));
        }
    }


    @PostMapping("/{username}/cambiaPassword")
    public ResponseEntity<?> cambiaPassword(
            @PathVariable String username,
            @RequestBody Map<String, String> passwordData) {
        try {
            String vecchiaPassword = passwordData.get("vecchiaPassword");
            String nuovaPassword = passwordData.get("nuovaPassword");

            // Trova l'utente
            Utente utente = userDao.findByUsername(username);
            if (utente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Utente non trovato"));
            }

            // Trova le credenziali
            Credenziali credenziali = credenzialiDao.findByIdUtente(utente.getId());
            if (credenziali == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Credenziali non trovate"));
            }

            // Verifica la vecchia password
            boolean passwordValida = passwordService.verifyPassword(
                    vecchiaPassword,
                    credenziali.getPasswordHash()
            );

            if (!passwordValida) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Vecchia password non corretta"));
            }

            // Valida la nuova password
            PasswordValidator.ValidationResult validationResult =
                    passwordValidator.validate(nuovaPassword);

            if (!validationResult.isValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", validationResult.getErrorMessage()));
            }

            // Hash della nuova password
            String nuovoHash = passwordService.hashPassword(nuovaPassword);

            // Salva nel database usando updatePassword esistente
            int rowsAffected = credenzialiDao.updatePassword(utente.getId(), nuovoHash);

            if (rowsAffected > 0) {
                return ResponseEntity.ok(Map.of(
                        "message", "Password cambiata con successo"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Errore durante il cambio password"));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Errore di sistema: " + ex.getMessage()));
        }
    }
}
