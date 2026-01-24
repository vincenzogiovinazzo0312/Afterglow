package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.Credenziali;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class CredenzialiDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Credenziali findByIdUtente(Integer idUtente) {

        String sql = "SELECT id_utente, password_hash FROM credenziali WHERE id_utente = ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{idUtente},
                    this::mapRowToCredenziali
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int insert(Credenziali credenziali) {
        String sql = "INSERT INTO credenziali (id_utente, password_hash) VALUES (?, ?)";

        return jdbcTemplate.update(sql,
                credenziali.getIdUtente(),
                credenziali.getPasswordHash()
        );
    }

    public int updatePassword(Integer idUtente, String passwordHash) {
        String sql = "UPDATE credenziali SET password_hash = ? WHERE id_utente = ?";
        return jdbcTemplate.update(sql, passwordHash, idUtente);
    }

    public int delete(Integer idUtente) {
        String sql = "DELETE FROM credenziali WHERE id_utente = ?";
        return jdbcTemplate.update(sql, idUtente);
    }

    private Credenziali mapRowToCredenziali(ResultSet rs, int rowNum) throws SQLException {
        Credenziali credenziali = new Credenziali();
        credenziali.setIdUtente(rs.getInt("id_utente"));
        credenziali.setPasswordHash(rs.getString("password_hash"));
        return credenziali;
    }
}
