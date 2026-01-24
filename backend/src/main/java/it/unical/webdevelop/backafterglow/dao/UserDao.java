package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.Utente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import it.unical.webdevelop.backafterglow.dao.UtenteBanditoDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Utente findByUsername(String username) {
        String sql = "SELECT id, username, nome, cognome, telefono, email, ruolo " +
                "FROM utenti WHERE username = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{username},
                    this::mapRowToUtente
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Optional<Utente> findById(Integer id) {
        String sql = "SELECT id, username, nome, cognome, telefono, email, ruolo " +
                "FROM utenti WHERE id = ?";

        try {
            Utente utente = jdbcTemplate.queryForObject(sql,
                    new Object[]{id},
                    this::mapRowToUtente
            );
            return Optional.ofNullable(utente);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int insert(Utente utente) {
        String sql = "INSERT INTO utenti (username, nome, cognome, telefono, email, ruolo) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                utente.getUsername(),
                utente.getNome(),
                utente.getCognome(),
                utente.getTelefono(),
                utente.getEmail(),
                utente.getRuolo()
        );
    }

    public int update(Utente utente) {
        String sql = "UPDATE utenti SET username = ?, nome = ?, cognome = ?, " +
                "telefono = ?, email = ?, ruolo = ? WHERE id = ?";

        return jdbcTemplate.update(sql,
                utente.getUsername(),
                utente.getNome(),
                utente.getCognome(),
                utente.getTelefono(),
                utente.getEmail(),
                utente.getRuolo(),
                utente.getId()
        );
    }

    public int updateByUsername(Utente utente) {
        String sql = "UPDATE utenti SET nome = ?, cognome = ?, " +
                "telefono = ?, email = ? WHERE username = ?";

        return jdbcTemplate.update(sql,
                utente.getNome(),
                utente.getCognome(),
                utente.getTelefono(),
                utente.getEmail(),
                utente.getUsername()
        );
    }

    public int delete(Integer id) {
        String sql = "DELETE FROM utenti WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

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
}
