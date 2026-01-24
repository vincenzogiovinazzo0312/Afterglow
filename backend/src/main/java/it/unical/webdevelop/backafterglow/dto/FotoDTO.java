package it.unical.webdevelop.backafterglow.dto;

public class FotoDTO {
    private Integer idFoto;
    private Integer idAlbum;
    private String percorso;
    private String nomeAlbum;

    // Costruttori
    public FotoDTO() {}

    public FotoDTO(Integer idFoto, Integer idAlbum, String percorso) {
        this.idFoto = idFoto;
        this.idAlbum = idAlbum;
        this.percorso = percorso;
    }

    public FotoDTO(Integer idFoto, Integer idAlbum, String percorso, String nomeAlbum) {
        this.idFoto = idFoto;
        this.idAlbum = idAlbum;
        this.percorso = percorso;
        this.nomeAlbum = nomeAlbum;
    }

    // Getters e Setters
    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public Integer getIdAlbum() {
        return idAlbum;
    }

    public void setIdAlbum(Integer idAlbum) {
        this.idAlbum = idAlbum;
    }

    public String getPercorso() {
        return percorso;
    }

    public void setPercorso(String percorso) {
        this.percorso = percorso;
    }

    public String getNomeAlbum() {
        return nomeAlbum;
    }

    public void setNomeAlbum(String nomeAlbum) {
        this.nomeAlbum = nomeAlbum;
    }
}
