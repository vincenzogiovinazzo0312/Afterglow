package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.IscrizioneEvento;
import it.unical.webdevelop.backafterglow.dto.IscrizioneEventoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class IscrizioneEventoDao {

    private final JdbcTemplate jdbcTemplate;

    public IscrizioneEventoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean insert(IscrizioneEventoDTO iscrizione) {
        String query = "INSERT INTO iscrizioni_eventi (evento_id, utente_id, nome, cognome, telefono) VALUES (?, ?, ?, ?, ?)";
        int rows = jdbcTemplate.update(query,
                iscrizione.getEventoId(),
                iscrizione.getUtenteId(),
                iscrizione.getNome(),
                iscrizione.getCognome(),
                iscrizione.getTelefono()
        );
        return rows > 0;
    }

    public List<IscrizioneEvento> findByEventoId(Long eventoId) {
        String query = """
            SELECT id, evento_id, utente_id, nome, cognome, telefono, entrato, created_at
            FROM iscrizioni_eventi
            WHERE evento_id = ?
            ORDER BY created_at DESC
        """;
        return jdbcTemplate.query(query, (rs, rowNum) -> {
            IscrizioneEvento iscrizione = new IscrizioneEvento();
            iscrizione.setId(rs.getLong("id"));
            iscrizione.setEventoId(rs.getLong("evento_id"));

            Object utenteIdObj = rs.getObject("utente_id");
            if (utenteIdObj != null) {
                iscrizione.setUtenteId(((Number) utenteIdObj).intValue());
            }

            iscrizione.setNome(rs.getString("nome"));
            iscrizione.setCognome(rs.getString("cognome"));
            iscrizione.setTelefono(rs.getString("telefono"));
            iscrizione.setEntrato(rs.getBoolean("entrato"));

            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            if (createdAtTimestamp != null) {
                iscrizione.setCreatedAt(createdAtTimestamp.toLocalDateTime());
            }

            return iscrizione;
        }, eventoId);
    }

    public int countByEventoId(Long eventoId) {
        String query = "SELECT COUNT(*) FROM iscrizioni_eventi WHERE evento_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, eventoId);
        return count != null ? count : 0;
    }

    public boolean existsByEventoIdAndTelefono(Long eventoId, String telefono) {
        String query = "SELECT COUNT(*) FROM iscrizioni_eventi WHERE evento_id = ? AND telefono = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, eventoId, telefono);
        return count != null && count > 0;
    }

    public boolean existsByEventoIdAndUtenteId(Long eventoId, Integer utenteId) {
        String query = "SELECT COUNT(*) FROM iscrizioni_eventi WHERE evento_id = ? AND utente_id = ?";
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, eventoId, utenteId);
        return count != null && count > 0;
    }

    public boolean deleteById(Long id) {
        String query = "DELETE FROM iscrizioni_eventi WHERE id = ?";
        int rows = jdbcTemplate.update(query, id);
        return rows > 0;
    }

    public IscrizioneEvento findById(Long id) {
        String query = """
            SELECT id, evento_id, utente_id, nome, cognome, telefono, entrato, created_at
            FROM iscrizioni_eventi
            WHERE id = ?
        """;
        List<IscrizioneEvento> list = jdbcTemplate.query(query, (rs, rowNum) -> {
            IscrizioneEvento iscrizione = new IscrizioneEvento();
            iscrizione.setId(rs.getLong("id"));
            iscrizione.setEventoId(rs.getLong("evento_id"));

            // CORREZIONE: gestisci correttamente utente_id che può essere NULL
            Object utenteIdObj = rs.getObject("utente_id");
            if (utenteIdObj != null) {
                iscrizione.setUtenteId(((Number) utenteIdObj).intValue());
            }

            iscrizione.setNome(rs.getString("nome"));
            iscrizione.setCognome(rs.getString("cognome"));
            iscrizione.setTelefono(rs.getString("telefono"));
            iscrizione.setEntrato(rs.getBoolean("entrato"));

            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            if (createdAtTimestamp != null) {
                iscrizione.setCreatedAt(createdAtTimestamp.toLocalDateTime());
            }

            return iscrizione;
        }, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean updateEntrato(Long id, boolean entrato) {
        String query = "UPDATE iscrizioni_eventi SET entrato = ? WHERE id = ?";
        int rows = jdbcTemplate.update(query, entrato, id);
        return rows > 0;
    }
}
