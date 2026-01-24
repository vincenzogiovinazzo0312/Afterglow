package it.unical.webdevelop.backafterglow.model;

import java.sql.Timestamp;

public class Foto {
    private Integer idFoto;
    private Integer idAlbum;
    private String percorso;
    private Timestamp dataCaricamento;

    // Costruttori
    public Foto() {}

    public Foto(Integer idFoto, Integer idAlbum, String percorso) {
        this.idFoto = idFoto;
        this.idAlbum = idAlbum;
        this.percorso = percorso;
    }

    public Foto(Integer idAlbum, String percorso) {
        this.idAlbum = idAlbum;
        this.percorso = percorso;
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

    public Timestamp getDataCaricamento() {
        return dataCaricamento;
    }

    public void setDataCaricamento(Timestamp dataCaricamento) {
        this.dataCaricamento = dataCaricamento;
    }

    @Override
    public String toString() {
        return "Foto{" +
                "idFoto=" + idFoto +
                ", idAlbum=" + idAlbum +
                ", percorso='" + percorso + '\'' +
                ", dataCaricamento=" + dataCaricamento +
                '}';
    }
}
