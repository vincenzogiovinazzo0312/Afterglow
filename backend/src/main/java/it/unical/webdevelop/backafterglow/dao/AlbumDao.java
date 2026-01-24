package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AlbumDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Trova tutti gli album
    public List<Album> findAll() {
        String sql = "SELECT id, nome, descrizione, foto_copertina, data_creazione, data_modifica " +
                "FROM albums ORDER BY data_creazione DESC";
        return jdbcTemplate.query(sql, this::mapRowToAlbum);
    }

    // Trova album per ID
    public Optional<Album> findById(Integer id) {
        String sql = "SELECT id, nome, descrizione, foto_copertina, data_creazione, data_modifica " +
                "FROM albums WHERE id = ?";
        try {
            Album album = jdbcTemplate.queryForObject(sql, new Object[]{id}, this::mapRowToAlbum);
            return Optional.ofNullable(album);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Cerca album per nome
    public List<Album> findByNome(String nome) {
        String sql = "SELECT id, nome, descrizione, foto_copertina, data_creazione, data_modifica " +
                "FROM albums WHERE LOWER(nome) LIKE LOWER(?)";
        String pattern = "%" + nome + "%";
        return jdbcTemplate.query(sql, this::mapRowToAlbum, pattern);
    }

    // Inserisci nuovo album
    public Integer insert(Album album) {
        String sql = "INSERT INTO albums (nome, descrizione, foto_copertina) VALUES (?, ?, ?) RETURNING id";

        return jdbcTemplate.queryForObject(sql, Integer.class,
                album.getNome(),
                album.getDescrizione(),
                album.getFotoCopertina()
        );
    }

    // Aggiorna album
    public int update(Album album) {
        String sql = "UPDATE albums SET nome = ?, descrizione = ?, foto_copertina = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                album.getNome(),
                album.getDescrizione(),
                album.getFotoCopertina(),
                album.getId()
        );
    }

    // Elimina album
    public int delete(Integer id) {
        String sql = "DELETE FROM albums WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    // Conta foto per album
    public int countFotoByAlbumId(Integer idAlbum) {
        String sql = "SELECT COUNT(*) FROM foto WHERE id_album = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idAlbum);
        return count != null ? count : 0;
    }

    // Mapper
    private Album mapRowToAlbum(ResultSet rs, int rowNum) throws SQLException {
        Album album = new Album();
        album.setId(rs.getInt("id"));
        album.setNome(rs.getString("nome"));
        album.setDescrizione(rs.getString("descrizione"));
        album.setFotoCopertina(rs.getString("foto_copertina"));
        album.setDataCreazione(rs.getTimestamp("data_creazione"));
        album.setDataModifica(rs.getTimestamp("data_modifica"));
        return album;
    }
}
