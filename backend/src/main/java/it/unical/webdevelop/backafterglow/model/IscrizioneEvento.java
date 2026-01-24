package it.unical.webdevelop.backafterglow.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class IscrizioneEvento {

    private Long id;
    private Long eventoId;
    private Integer utenteId;

    private String nome;
    private String cognome;
    private String telefono;

    private Boolean entrato = false;

    private LocalDateTime createdAt;

    // Lazy loaded
    private Evento evento;
}
