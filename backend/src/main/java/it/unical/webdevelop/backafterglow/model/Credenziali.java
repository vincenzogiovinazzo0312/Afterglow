package it.unical.webdevelop.backafterglow.model;

public class Credenziali {
    private Integer idUtente;
    private String passwordHash;

    public Credenziali() {}

    public Integer getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Integer idUtente) {
        this.idUtente = idUtente;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
