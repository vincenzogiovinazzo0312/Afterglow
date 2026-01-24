package it.unical.webdevelop.backafterglow.model;

import java.sql.Timestamp;

public class Album {
    private Integer id;
    private String nome;
    private String descrizione;
    private String fotoCopertina;
    private Timestamp dataCreazione;
    private Timestamp dataModifica;

    // Costruttori
    public Album() {}

    public Album(Integer id, String nome, String descrizione, String fotoCopertina) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.fotoCopertina = fotoCopertina;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getFotoCopertina() {
        return fotoCopertina;
    }

    public void setFotoCopertina(String fotoCopertina) {
        this.fotoCopertina = fotoCopertina;
    }

    public Timestamp getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(Timestamp dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public Timestamp getDataModifica() {
        return dataModifica;
    }

    public void setDataModifica(Timestamp dataModifica) {
        this.dataModifica = dataModifica;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", fotoCopertina='" + fotoCopertina + '\'' +
                ", dataCreazione=" + dataCreazione +
                ", dataModifica=" + dataModifica +
                '}';
    }
}
