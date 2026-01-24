package it.unical.webdevelop.backafterglow.controller;

import it.unical.webdevelop.backafterglow.dto.UtenteBanditoDTO;
import it.unical.webdevelop.backafterglow.proxy.UtenteBanditoProxy;
import it.unical.webdevelop.backafterglow.services.UtenteBanditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/utenti")
@CrossOrigin(origins = "*")
public class UtenteBanditoController {

    @Autowired
    private UtenteBanditoService utenteBanditoService;

    // ============= ENDPOINT CON PROXY DTO (pubblici) =============

    //Ritorna solo id, username, nome, cognome
    @GetMapping("/non-banditi")
    public ResponseEntity<List<UtenteBanditoProxy>> getUtentiNonBanditi() {
        try {
            List<UtenteBanditoProxy> utenti = utenteBanditoService.getUtentiNonBanditi();
            return ResponseEntity.ok(utenti);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //Ritorna solo id, username, nome, cognome
    @GetMapping("/banditi")
    public ResponseEntity<List<UtenteBanditoProxy>> getUtentiBanditi() {
        try {
            List<UtenteBanditoProxy> utenti = utenteBanditoService.getUtentiBanditi();
            return ResponseEntity.ok(utenti);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //Ritorna solo id, username, nome, cognome
    @GetMapping("/cerca")
    public ResponseEntity<List<UtenteBanditoProxy>> cercaUtenti(
            @RequestParam(required = false, defaultValue = "") String nome,
            @RequestParam(required = false, defaultValue = "") String cognome) {
        try {
            List<UtenteBanditoProxy> utenti = utenteBanditoService.cercaUtenti(nome, cognome);
            return ResponseEntity.ok(utenti);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============= ENDPOINT CON DTO COMPLETO (admin) =============

    //Ritorna tutti i campi
    @GetMapping("/non-banditi/completo")
    public ResponseEntity<List<UtenteBanditoDTO>> getUtentiNonBanditiCompleto() {
        try {
            List<UtenteBanditoDTO> utenti = utenteBanditoService.getUtentiNonBanditiCompleto();
            return ResponseEntity.ok(utenti);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //Ritorna tutti i campi
    @GetMapping("/banditi/completo")
    public ResponseEntity<List<UtenteBanditoDTO>> getUtentiBanditiCompleto() {
        try {
            List<UtenteBanditoDTO> utenti = utenteBanditoService.getUtentiBanditiCompleto();
            return ResponseEntity.ok(utenti);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    //Ritorna tutti i campi di un utente specifico
    @GetMapping("/{id}/completo")
    public ResponseEntity<UtenteBanditoDTO> getUtenteCompleto(@PathVariable Integer id) {
        try {
            UtenteBanditoDTO utente = utenteBanditoService.getUtenteByIdCompleto(id);
            return ResponseEntity.ok(utente);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ============= OPERAZIONI BANDISCI/RIPRISTINA =============

    //funzione bandisci utente
    @PostMapping("/bandisci/{id}")
    public ResponseEntity<Map<String, String>> bandisciUtente(@PathVariable Integer id) {
        try {
            utenteBanditoService.bandisciUtente(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utente bandito con successo");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    //funzione riprista utente precedentemente bandito
    @PostMapping("/ripristina/{id}")
    public ResponseEntity<Map<String, String>> ripristinaUtente(@PathVariable Integer id) {
        try {
            utenteBanditoService.ripristinaUtente(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Utente ripristinato con successo");
            response.put("id", id.toString());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server");
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
