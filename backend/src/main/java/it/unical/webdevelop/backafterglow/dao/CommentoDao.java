package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.dto.CommentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // RowMapper per CommentoDTO
    private final RowMapper<CommentoDTO> commentoRowMapper = new RowMapper<CommentoDTO>() {
        @Override
        public CommentoDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            CommentoDTO dto = new CommentoDTO();
            dto.setIdCommento(rs.getLong("id_commento"));
            dto.setIdFoto(rs.getLong("id_foto"));
            dto.setUsername(rs.getString("username"));
            dto.setNomeUtente(rs.getString("nome_utente"));
            dto.setTesto(rs.getString("testo"));
            dto.setDataCommento(rs.getTimestamp("data_commento").toLocalDateTime());
            return dto;
        }
    };

    // Ottieni commenti per foto con nome utente (usa username)
    public List<CommentoDTO> getCommentiByFoto(Long idFoto) {
        String sql = "SELECT c.id_commento, c.id_foto, c.username, " +
                "(u.nome || ' ' || u.cognome) AS nome_utente, " +
                "c.testo, c.data_commento " +
                "FROM commenti c " +
                "JOIN utenti u ON c.username = u.username " +
                "WHERE c.id_foto = ? " +
                "ORDER BY c.data_commento DESC";

        return jdbcTemplate.query(sql, commentoRowMapper, idFoto);
    }

    // Aggiungi commento (usa username)
    public void addCommento(Long idFoto, String username, String testo) {
        String sql = "INSERT INTO commenti (id_foto, username, testo, data_commento) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, idFoto, username, testo);
    }

    // Elimina commento
    public void deleteCommento(Long idCommento) {
        String sql = "DELETE FROM commenti WHERE id_commento = ?";
        jdbcTemplate.update(sql, idCommento);
    }

    // Conta commenti per foto
    public int countCommentiByFoto(Long idFoto) {
        String sql = "SELECT COUNT(*) FROM commenti WHERE id_foto = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idFoto);
        return count != null ? count : 0;
    }
}
