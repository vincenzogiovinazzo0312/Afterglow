package it.unical.webdevelop.backafterglow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventoDTO {

    private Long id;
    private String titolo;
    private LocalDateTime data;
    private String immagine;
    private String descrizione;

    public EventoDTO() {
    }

    public EventoDTO(Long id, String titolo, String descrizione,
                     LocalDateTime data, String immagine) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.data = data;
        this.immagine = immagine;
    }
}
