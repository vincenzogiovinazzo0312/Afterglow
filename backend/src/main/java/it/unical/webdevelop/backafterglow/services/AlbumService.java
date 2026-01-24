package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.AlbumDao;
import it.unical.webdevelop.backafterglow.dao.FotoDao;
import it.unical.webdevelop.backafterglow.dto.AlbumDTO;
import it.unical.webdevelop.backafterglow.model.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    @Autowired
    private AlbumDao albumDao;

    @Autowired
    private FotoDao fotoDao;

    private AlbumDTO convertToDTO(Album album) {
        int numeroFoto = albumDao.countFotoByAlbumId(album.getId());
        return new AlbumDTO(
                album.getId(),
                album.getNome(),
                album.getDescrizione(),
                album.getFotoCopertina(),
                numeroFoto
        );
    }

    public List<AlbumDTO> getAllAlbums() {
        return albumDao.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AlbumDTO getAlbumById(Integer id) {
        Album album = albumDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Album non trovato con id: " + id));
        return convertToDTO(album);
    }

    //Crea nuovo album
    @Transactional
    public Integer createAlbum(Album album) {
        return albumDao.insert(album);
    }

    //Aggiorna album
    @Transactional
    public void updateAlbum(Album album) {
        if (!albumDao.findById(album.getId()).isPresent()) {
            throw new RuntimeException("Album non trovato con id: " + album.getId());
        }
        albumDao.update(album);
    }

    //Elimina album
    @Transactional
    public void deleteAlbum(Integer id) {
        if (!albumDao.findById(id).isPresent()) {
            throw new RuntimeException("Album non trovato con id: " + id);
        }
        // Le foto su Cloudinary vengono eliminate dal Controller
        albumDao.delete(id);
    }

    // Cerca album per nome
    public List<AlbumDTO> searchAlbumsByName(String nome) {
        return albumDao.findByNome(nome)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
