package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.FotoDao;
import it.unical.webdevelop.backafterglow.dto.FotoDTO;
import it.unical.webdevelop.backafterglow.model.Foto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FotoService {

    @Autowired
    private FotoDao fotoDao;

    private FotoDTO convertToDTO(Foto foto) {
        return new FotoDTO(
                foto.getIdFoto(),
                foto.getIdAlbum(),
                foto.getPercorso()
        );
    }

    public List<FotoDTO> getAllFoto() {
        return fotoDao.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FotoDTO getFotoById(Integer id) {
        Foto foto = fotoDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Foto non trovata con id: " + id));
        return convertToDTO(foto);
    }

    public List<FotoDTO> getFotoByAlbumId(Integer albumId) {
        return fotoDao.findByAlbumId(albumId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Integer createFoto(Foto foto) {
        return fotoDao.insert(foto);
    }

    @Transactional
    public int[] createFotoBatch(List<Foto> foto) {
        return fotoDao.insertBatch(foto);
    }

    @Transactional
    public void updateFoto(Foto foto) {
        if (!fotoDao.findById(foto.getIdFoto()).isPresent()) {
            throw new RuntimeException("Foto non trovata con id: " + foto.getIdFoto());
        }
        fotoDao.update(foto);
    }

    @Transactional
    public void deleteFoto(Integer id) {
        if (!fotoDao.findById(id).isPresent()) {
            throw new RuntimeException("Foto non trovata con id: " + id);
        }
        fotoDao.delete(id);
    }

    @Transactional
    public void deleteFotoByAlbumId(Integer albumId) {
        fotoDao.deleteByAlbumId(albumId);
    }
}
