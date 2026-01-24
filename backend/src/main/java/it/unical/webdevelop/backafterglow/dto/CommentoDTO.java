package it.unical.webdevelop.backafterglow.dto;

import java.time.LocalDateTime;

public class CommentoDTO {
    private Long idCommento;
    private Long idFoto;
    private String username;
    private String nomeUtente;
    private String testo;
    private LocalDateTime dataCommento;

    public CommentoDTO() {}

    public CommentoDTO(Long idCommento, Long idFoto, String username, String nomeUtente, String testo, LocalDateTime dataCommento) {
        this.idCommento = idCommento;
        this.idFoto = idFoto;
        this.username = username;
        this.nomeUtente = nomeUtente;
        this.testo = testo;
        this.dataCommento = dataCommento;
    }

    // Getters e Setters
    public Long getIdCommento() { return idCommento; }
    public void setIdCommento(Long idCommento) { this.idCommento = idCommento; }

    public Long getIdFoto() { return idFoto; }
    public void setIdFoto(Long idFoto) { this.idFoto = idFoto; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNomeUtente() { return nomeUtente; }
    public void setNomeUtente(String nomeUtente) { this.nomeUtente = nomeUtente; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public LocalDateTime getDataCommento() { return dataCommento; }
    public void setDataCommento(LocalDateTime dataCommento) { this.dataCommento = dataCommento; }
}
