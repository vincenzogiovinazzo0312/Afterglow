package it.unical.webdevelop.backafterglow.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IscrizioneEventoDTO {
    private Long eventoId;
    private Integer utenteId;
    private String nome;
    private String cognome;
    private String telefono;
}
