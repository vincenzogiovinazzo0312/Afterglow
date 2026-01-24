package it.unical.webdevelop.backafterglow.proxy;

public class UtenteBanditoProxy {
    private Integer id;
    private String username;
    private String nome;
    private String cognome;

    // Costruttori
    public UtenteBanditoProxy() {}

    public UtenteBanditoProxy(Integer id, String username, String nome, String cognome) {
        this.id = id;
        this.username = username;
        this.nome = nome;
        this.cognome = cognome;
    }

    // Getters e Setters
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
}
