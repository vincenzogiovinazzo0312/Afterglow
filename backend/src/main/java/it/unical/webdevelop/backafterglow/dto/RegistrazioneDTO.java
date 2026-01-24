package it.unical.webdevelop.backafterglow.dto;

public class RegistrazioneDTO {
    private String username;
    private String password;
    private String nome;
    private String cognome;
    private String telefono;
    private String email;
    private Integer ruolo;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getRuolo() { return ruolo; }
    public void setRuolo(Integer ruolo) { this.ruolo = ruolo; }
}
