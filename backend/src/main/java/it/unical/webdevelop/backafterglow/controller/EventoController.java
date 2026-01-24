package it.unical.webdevelop.backafterglow.controller;

import it.unical.webdevelop.backafterglow.dto.EventoDTO;
import it.unical.webdevelop.backafterglow.services.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/eventi")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoDTO>> getEventiProssimi() {
        try {
            System.out.println("🌐 /api/eventi chiamato - " + LocalDateTime.now());
            long start = System.currentTimeMillis();
            List<EventoDTO> eventi = eventoService.getEventiProssimi();
            System.out.println("✅ Eventi caricati: " + eventi.size() + " in " + (System.currentTimeMillis() - start) + "ms");
            return ResponseEntity.ok(eventi);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventoDTO>> getAllEventi() {
        try {
            return ResponseEntity.ok(eventoService.getAllEventi());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> getEventoById(@PathVariable Long id) {
        try {
            Optional<EventoDTO> evento = eventoService.getEventoById(id);
            return evento.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createEvento(@RequestBody EventoDTO eventoDTO) {
        try {
            boolean created = eventoService.createEvento(eventoDTO);
            if (created) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Evento creato con successo");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateEvento(@PathVariable Long id,
                                                            @RequestBody EventoDTO eventoDTO) {
        try {
            eventoDTO.setId(id);
            boolean updated = eventoService.updateEvento(eventoDTO);
            if (updated) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Evento aggiornato con successo");
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id) {
        try {
            boolean deleted = eventoService.deleteEvento(id);
            return deleted ? ResponseEntity.noContent().build()
                    : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // CREATE + UPLOAD
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadEvento(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titolo") String titolo,
            @RequestParam("descrizione") String descrizione,
            @RequestParam("data") String data
    ) {
        try {
            String imageUrl = eventoService.uploadImmagineEvento(file);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime dataEvento = LocalDateTime.parse(data, formatter);

            EventoDTO dto = new EventoDTO();
            dto.setTitolo(titolo);
            dto.setDescrizione(descrizione);
            dto.setData(dataEvento);
            dto.setImmagine(imageUrl);

            boolean created = eventoService.createEvento(dto);
            if (created) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Evento creato con successo");
                response.put("imageUrl", imageUrl);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
