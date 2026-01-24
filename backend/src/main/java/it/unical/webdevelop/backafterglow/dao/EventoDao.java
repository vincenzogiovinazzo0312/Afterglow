package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.Evento;
import it.unical.webdevelop.backafterglow.dto.EventoDTO;
import it.unical.webdevelop.backafterglow.dao.IscrizioneEventoDao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class EventoDao {

    private final JdbcTemplate jdbcTemplate;
    private final IscrizioneEventoDao iscrizioneEventoDAO;

    public EventoDao(JdbcTemplate jdbcTemplate, IscrizioneEventoDao iscrizioneEventoDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.iscrizioneEventoDAO = iscrizioneEventoDAO;
    }

    // RowMapper semplice -> Evento
    private RowMapper<Evento> getSimpleRowMapper() {
        return (rs, rowNum) -> {
            Evento evento = new Evento();
            evento.setId(rs.getLong("id"));
            evento.setTitolo(rs.getString("titolo"));

            Timestamp dataTimestamp = rs.getTimestamp("data");
            if (dataTimestamp != null) {
                evento.setData(dataTimestamp.toLocalDateTime());
            }

            evento.setImmagine(rs.getString("immagine"));
            evento.setDescrizione(rs.getString("descrizione"));

            Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
            if (createdAtTimestamp != null) {
                evento.setCreatedAt(createdAtTimestamp.toLocalDateTime());
            }

            Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
            if (updatedAtTimestamp != null) {
                evento.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
            }

            return evento;
        };
    }

    // ===== LETTURA =====
    public List<Evento> findEventiProssimi() {
        String query = "SELECT * FROM eventi WHERE data > ? ORDER BY data ASC";
        System.out.println("🔍 findEventiProssimi() chiamata");
        return jdbcTemplate.query(
                query,
                getSimpleRowMapper(),
                Timestamp.valueOf(LocalDateTime.now())
        );
    }

    public List<Evento> findAll() {
        String query = "SELECT * FROM eventi ORDER BY data ASC";
        return jdbcTemplate.query(query, getSimpleRowMapper());
    }

    public Optional<Evento> findById(Long id) {
        String query = "SELECT * FROM eventi WHERE id = ?";
        List<Evento> eventi = jdbcTemplate.query(query, getSimpleRowMapper(), id);
        return eventi.isEmpty() ? Optional.empty() : Optional.of(eventi.get(0));
    }

    // ===== SCRITTURA (usa EventoDTO) =====
    public boolean insert(EventoDTO evento) {
        String query = "INSERT INTO eventi (titolo, data, immagine, descrizione) VALUES (?, ?, ?, ?)";
        int rows = jdbcTemplate.update(
                query,
                evento.getTitolo(),
                Timestamp.valueOf(evento.getData()),
                evento.getImmagine(),
                evento.getDescrizione()
        );
        return rows > 0;
    }

    public boolean update(EventoDTO evento) {
        String query = "UPDATE eventi SET titolo = ?, data = ?, immagine = ?, descrizione = ?, updated_at = ? WHERE id = ?";
        int rows = jdbcTemplate.update(
                query,
                evento.getTitolo(),
                Timestamp.valueOf(evento.getData()),
                evento.getImmagine(),
                evento.getDescrizione(),
                Timestamp.valueOf(LocalDateTime.now()),
                evento.getId()
        );
        return rows > 0;
    }

    public boolean deleteById(Long id) {
        String query = "DELETE FROM eventi WHERE id = ?";
        int rows = jdbcTemplate.update(query, id);
        return rows > 0;
    }
}
