package it.unical.webdevelop.backafterglow.model;

public class UtenteGoogle {
    private Long id;
    private String googleId;
    private String email;
    private String nome;
    private String cognome;
    private String pictureUrl;
    private String locale;
    private Integer ruolo;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGoogleId() {
        return googleId;
    }
    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCognome() {
        return cognome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public String getPictureUrl() {
        return pictureUrl;
    }
    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Integer getRuolo() {
        return ruolo;
    }
    public void setRuolo(Integer ruolo) {
        this.ruolo = ruolo;
    }
}
