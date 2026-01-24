package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.Utente;
import it.unical.webdevelop.backafterglow.model.UtenteBandito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UtenteBanditoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Trova tutti gli utenti NON banditi (dalla tabella utenti)
    public List<Utente> findUtentiNonBanditi() {
        String sql = "SELECT id, username, nome, cognome, telefono, email, ruolo FROM utenti WHERE ruolo=1";
        return jdbcTemplate.query(sql, this::mapRowToUtente);
    }

    // Trova tutti gli utenti banditi (dalla tabella utenti_banditi)
    public List<UtenteBandito> findUtentiBanditi() {
        String sql = "SELECT id, username, nome, cognome, telefono, email, ruolo FROM utenti_banditi";
        return jdbcTemplate.query(sql, this::mapRowToUtenteBandito);
    }

    // Cerca utenti per nome e cognome nella tabella utenti
    public List<Utente> cercaUtentiNonBanditi(String nome, String cognome) {
        String sql = "SELECT id, username, nome, cognome, telefono, email, ruolo " +
                "FROM utenti " +
                "WHERE LOWER(nome) LIKE LOWER(?) AND LOWER(cognome) LIKE LOWER(?)";
        String nomePattern = "%" + (nome != null ? nome : "") + "%";
        String cognomePattern = "%" + (cognome != null ? cognome : "") + "%";
        return jdbcTemplate.query(sql, this::mapRowToUtente, nomePattern, cognomePattern);
    }

    // Cerca utenti banditi per nome e cognome
    public List<UtenteBandito> cercaUtentiBanditi(String nome, String cognome) {
        String sql = "SELECT id, username, nome, cognome, telefono, email, ruolo " +
                "FROM utenti_banditi " +
                "WHERE LOWER(nome) LIKE LOWER(?) AND LOWER(cognome) LIKE LOWER(?)";
        String nomePattern = "%" + (nome != null ? nome : "") + "%";
        String cognomePattern = "%" + (cognome != null ? cognome : "") + "%";
        return jdbcTemplate.query(sql, this::mapRowToUtenteBandito, nomePattern, cognomePattern);
    }

    // BANDISCI: Sposta l'utente da 'utenti' a 'utenti_banditi'
    public int bandisciUtente(Integer utenteId) {
        // 1. Recupera i dati dell'utente dalla tabella utenti
        String selectSql = "SELECT id, username, nome, cognome, telefono, email, ruolo FROM utenti WHERE id = ?";

        try {
            Utente utente = jdbcTemplate.queryForObject(selectSql, new Object[]{utenteId}, this::mapRowToUtente);

            // 2. Inserisci nella tabella utenti_banditi
            String insertSql = "INSERT INTO utenti_banditi (id, username, nome, cognome, telefono, email, ruolo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    utente.getId(),
                    utente.getUsername(),
                    utente.getNome(),
                    utente.getCognome(),
                    utente.getTelefono(),
                    utente.getEmail(),
                    utente.getRuolo()
            );

            // 3. Elimina dalla tabella utenti
            String deleteSql = "DELETE FROM utenti WHERE id = ?";
            return jdbcTemplate.update(deleteSql, utenteId);

        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Utente non trovato con id: " + utenteId);
        }
    }

    // RIPRISTINA: Sposta l'utente da 'utenti_banditi' a 'utenti'
    public int ripristinaUtente(Integer utenteId) {
        // 1. Recupera i dati dell'utente dalla tabella utenti_banditi
        String selectSql = "SELECT id, username, nome, cognome, telefono, email, ruolo FROM utenti_banditi WHERE id = ?";

        try {
            UtenteBandito utenteBandito = jdbcTemplate.queryForObject(selectSql, new Object[]{utenteId}, this::mapRowToUtenteBandito);

            // 2. Inserisci nella tabella utenti
            String insertSql = "INSERT INTO utenti (id, username, nome, cognome, telefono, email, ruolo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertSql,
                    utenteBandito.getId(),
                    utenteBandito.getUsername(),
                    utenteBandito.getNome(),
                    utenteBandito.getCognome(),
                    utenteBandito.getTelefono(),
                    utenteBandito.getEmail(),
                    utenteBandito.getRuolo()
            );

            // 3. Elimina dalla tabella utenti_banditi
            String deleteSql = "DELETE FROM utenti_banditi WHERE id = ?";
            return jdbcTemplate.update(deleteSql, utenteId);

        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Utente bandito non trovato con id: " + utenteId);
        }
    }

    // Verifica se un utente è bandito (esiste nella tabella utenti_banditi)
    public boolean isUtenteBandito(Integer utenteId) {
        String sql = "SELECT COUNT(*) FROM utenti_banditi WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, utenteId);
        return count != null && count > 0;
    }

    public boolean isBanditoUsername(String username) {
        String sql = "SELECT COUNT(*) FROM utenti_banditi WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }

    public boolean isBanditoEmail(String email){
        String sql = "SELECT COUNT(*) FROM utenti_banditi WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean isBanditoTelefono(String telefono){
        String sql = "SELECT COUNT(*) FROM utenti_banditi WHERE telefono = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, telefono);
        return count != null && count > 0;
    }

    // Mapper per Utente (tabella utenti)
    private Utente mapRowToUtente(ResultSet rs, int rowNum) throws SQLException {
        Utente utente = new Utente();
        utente.setId(rs.getInt("id"));
        utente.setUsername(rs.getString("username"));
        utente.setNome(rs.getString("nome"));
        utente.setCognome(rs.getString("cognome"));
        utente.setTelefono(rs.getString("telefono"));
        utente.setEmail(rs.getString("email"));
        utente.setRuolo(rs.getInt("ruolo"));
        return utente;
    }

    // Mapper per UtenteBandito (tabella utenti_banditi)
    private UtenteBandito mapRowToUtenteBandito(ResultSet rs, int rowNum) throws SQLException {
        UtenteBandito utenteBandito = new UtenteBandito();
        utenteBandito.setId(rs.getInt("id"));
        utenteBandito.setUsername(rs.getString("username"));
        utenteBandito.setNome(rs.getString("nome"));
        utenteBandito.setCognome(rs.getString("cognome"));
        utenteBandito.setTelefono(rs.getString("telefono"));
        utenteBandito.setEmail(rs.getString("email"));
        utenteBandito.setRuolo(rs.getInt("ruolo"));
        return utenteBandito;
    }
}
