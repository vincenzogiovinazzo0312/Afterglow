package it.unical.webdevelop.backafterglow.controller;

import it.unical.webdevelop.backafterglow.dto.IscrizioneEventoDTO;
import it.unical.webdevelop.backafterglow.dto.IscrizioneRapidaDTO;
import it.unical.webdevelop.backafterglow.model.IscrizioneEvento;
import it.unical.webdevelop.backafterglow.security.JwtTokenProvider;
import it.unical.webdevelop.backafterglow.services.IscrizioneEventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/iscrizioni")
public class IscrizioneEventoController {

    private final IscrizioneEventoService iscrizioneEventoService;
    private final JwtTokenProvider jwtTokenProvider;

    public IscrizioneEventoController(IscrizioneEventoService iscrizioneEventoService,
                                      JwtTokenProvider jwtTokenProvider) {
        this.iscrizioneEventoService = iscrizioneEventoService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> creaIscrizione(@RequestBody IscrizioneEventoDTO iscrizioneDTO) {
        try {
            boolean created = iscrizioneEventoService.creaIscrizione(iscrizioneDTO);
            if (created) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Registrazione completata con successo!");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("ERRORE creaIscrizione: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/rapida")
    public ResponseEntity<Map<String, String>> creaIscrizioneRapida(
            @RequestBody IscrizioneRapidaDTO iscrizioneRapidaDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token JWT mancante o non valido");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            String token = authHeader.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token JWT non valido o scaduto");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Integer utenteId = jwtTokenProvider.getUserIdFromToken(token);
            if (utenteId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "userId non trovato nel token JWT");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            boolean created = iscrizioneEventoService.creaIscrizioneRapida(iscrizioneRapidaDTO, utenteId);
            if (created) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Iscrizione completata con successo!");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            System.err.println("ERRORE creaIscrizioneRapida: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<IscrizioneEvento>> getIscrizioniEvento(@PathVariable Long eventoId) {
        try {
            List<IscrizioneEvento> iscrizioni = iscrizioneEventoService.getIscrizioniByEvento(eventoId);
            return ResponseEntity.ok(iscrizioni);
        } catch (Exception e) {
            System.err.println("ERRORE getIscrizioniEvento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/evento/{eventoId}/count")
    public ResponseEntity<Map<String, Integer>> contaIscrizioni(@PathVariable Long eventoId) {
        try {
            int count = iscrizioneEventoService.contaIscrizioni(eventoId);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERRORE contaIscrizioni: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/evento/{eventoId}/consentita")
    public ResponseEntity<Map<String, Boolean>> verificaIscrizioneConsentita(@PathVariable Long eventoId) {
        try {
            boolean consentita = iscrizioneEventoService.verificaIscrizioneConsentita(eventoId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("consentita", consentita);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERRORE verificaIscrizioneConsentita: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/evento/{eventoId}/iscritto")
    public ResponseEntity<Map<String, Boolean>> verificaIscrizioneUtente(
            @PathVariable Long eventoId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String token = authHeader.substring(7);
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer utenteId = jwtTokenProvider.getUserIdFromToken(token);
            if (utenteId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            boolean iscritto = iscrizioneEventoService.verificaUtenteIscritto(eventoId, utenteId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("iscritto", iscritto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("ERRORE verificaIscrizioneUtente: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIscrizione(@PathVariable Long id) {
        try {
            boolean deleted = iscrizioneEventoService.deleteIscrizione(id);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("ERRORE deleteIscrizione: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/stato")
    public ResponseEntity<Void> aggiornaStatoEntrato(
            @PathVariable Long id,
            @RequestParam boolean entrato) {
        try {
            boolean ok = iscrizioneEventoService.aggiornaStatoEntrato(id, entrato);
            return ok ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("ERRORE aggiornaStatoEntrato: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
