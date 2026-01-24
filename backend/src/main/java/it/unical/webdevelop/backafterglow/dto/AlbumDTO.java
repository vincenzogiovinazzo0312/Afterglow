package it.unical.webdevelop.backafterglow.dto;

public class AlbumDTO {
    private Integer id;
    private String nome;
    private String descrizione;
    private String fotoCopertina;
    private Integer numeroFoto;

    // Costruttori
    public AlbumDTO() {}

    public AlbumDTO(Integer id, String nome, String descrizione, String fotoCopertina) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.fotoCopertina = fotoCopertina;
    }

    public AlbumDTO(Integer id, String nome, String descrizione, String fotoCopertina, Integer numeroFoto) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.fotoCopertina = fotoCopertina;
        this.numeroFoto = numeroFoto;
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

    public Integer getNumeroFoto() {
        return numeroFoto;
    }

    public void setNumeroFoto(Integer numeroFoto) {
        this.numeroFoto = numeroFoto;
    }
}
