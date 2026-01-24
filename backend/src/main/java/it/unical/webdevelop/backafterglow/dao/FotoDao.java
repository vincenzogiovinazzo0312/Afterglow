package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.model.Foto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class FotoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Foto> findAll() {
        String sql = "SELECT id_foto, id_album, percorso, data_caricamento FROM foto ORDER BY data_caricamento DESC";
        return jdbcTemplate.query(sql, this::mapRowToFoto);
    }

    public Optional<Foto> findById(Integer idFoto) {
        String sql = "SELECT id_foto, id_album, percorso, data_caricamento FROM foto WHERE id_foto = ?";
        try {
            Foto foto = jdbcTemplate.queryForObject(sql, new Object[]{idFoto}, this::mapRowToFoto);
            return Optional.ofNullable(foto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Foto> findByAlbumId(Integer idAlbum) {
        String sql = "SELECT id_foto, id_album, percorso, data_caricamento FROM foto WHERE id_album = ? ORDER BY data_caricamento DESC";
        return jdbcTemplate.query(sql, this::mapRowToFoto, idAlbum);
    }

    public Integer insert(Foto foto) {
        String sql = "INSERT INTO foto (id_album, percorso) VALUES (?, ?) RETURNING id_foto";
        return jdbcTemplate.queryForObject(sql, Integer.class,
                foto.getIdAlbum(),
                foto.getPercorso()
        );
    }

    public int[] insertBatch(List<Foto> fotoList) {
        String sql = "INSERT INTO foto (id_album, percorso) VALUES (?, ?)";

        return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Foto foto = fotoList.get(i);
                ps.setInt(1, foto.getIdAlbum());
                ps.setString(2, foto.getPercorso());
            }

            @Override
            public int getBatchSize() {
                return fotoList.size();
            }
        });
    }

    public int update(Foto foto) {
        String sql = "UPDATE foto SET id_album = ?, percorso = ? WHERE id_foto = ?";
        return jdbcTemplate.update(sql,
                foto.getIdAlbum(),
                foto.getPercorso(),
                foto.getIdFoto()
        );
    }

    public int delete(Integer idFoto) {
        String sql = "DELETE FROM foto WHERE id_foto = ?";
        return jdbcTemplate.update(sql, idFoto);
    }

    public int deleteByAlbumId(Integer idAlbum) {
        String sql = "DELETE FROM foto WHERE id_album = ?";
        return jdbcTemplate.update(sql, idAlbum);
    }

    private Foto mapRowToFoto(ResultSet rs, int rowNum) throws SQLException {
        Foto foto = new Foto();
        foto.setIdFoto(rs.getInt("id_foto"));
        foto.setIdAlbum(rs.getInt("id_album"));
        foto.setPercorso(rs.getString("percorso"));
        foto.setDataCaricamento(rs.getTimestamp("data_caricamento"));
        return foto;
    }
}
