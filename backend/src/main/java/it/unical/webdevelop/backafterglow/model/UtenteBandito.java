package it.unical.webdevelop.backafterglow.model;

public class UtenteBandito {
    private Integer id;
    private String username;
    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private Integer ruolo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRuolo() {
        return ruolo;
    }

    public void setRuolo(Integer ruolo) {
        this.ruolo = ruolo;
    }
}
