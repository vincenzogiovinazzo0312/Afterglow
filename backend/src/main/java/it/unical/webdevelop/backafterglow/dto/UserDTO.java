package it.unical.webdevelop.backafterglow.dto;

public class UserDTO {
    private Long id;
    private String username;
    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private Integer ruolo;

    // Costruttori
    public UserDTO() {}

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
