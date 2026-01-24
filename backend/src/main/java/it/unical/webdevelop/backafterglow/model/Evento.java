package it.unical.webdevelop.backafterglow.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Evento {
    private Long id;
    private String titolo;
    private LocalDateTime data;
    private String immagine;
    private String descrizione;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Lazy loaded
    private List<IscrizioneEvento> iscrizioni;
}