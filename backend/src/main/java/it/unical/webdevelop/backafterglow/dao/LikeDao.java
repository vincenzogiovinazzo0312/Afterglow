package it.unical.webdevelop.backafterglow.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Conta like per una foto
    public int countLikesByFoto(Long idFoto) {
        String sql = "SELECT COUNT(*) FROM likes WHERE id_foto = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idFoto);
        return count != null ? count : 0;
    }

    // Verifica se utente ha già messo like (usa username)
    public boolean hasUserLiked(Long idFoto, String username) {
        String sql = "SELECT COUNT(*) FROM likes WHERE id_foto = ? AND username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idFoto, username);
        return count != null && count > 0;
    }

    // Aggiungi like (usa username)
    public void addLike(Long idFoto, String username) {
        String sql = "INSERT INTO likes (id_foto, username, data_like) VALUES (?, ?, CURRENT_TIMESTAMP)";
        jdbcTemplate.update(sql, idFoto, username);
    }

    // Rimuovi like (usa username)
    public void removeLike(Long idFoto, String username) {
        String sql = "DELETE FROM likes WHERE id_foto = ? AND username = ?";
        jdbcTemplate.update(sql, idFoto, username);
    }
}
