package it.unical.webdevelop.backafterglow.controller;

import it.unical.webdevelop.backafterglow.dto.AlbumDTO;
import it.unical.webdevelop.backafterglow.model.Album;
import it.unical.webdevelop.backafterglow.model.Foto;
import it.unical.webdevelop.backafterglow.services.AlbumService;
import it.unical.webdevelop.backafterglow.services.CloudinaryService;
import it.unical.webdevelop.backafterglow.dao.FotoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/albums")
@CrossOrigin(origins = "*")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private FotoDao fotoDao;

    @GetMapping
    public ResponseEntity<List<AlbumDTO>> getAllAlbums() {
        try {
            List<AlbumDTO> albums = albumService.getAllAlbums();
            return ResponseEntity.ok(albums);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbum(@PathVariable Integer id) {
        try {
            AlbumDTO album = albumService.getAlbumById(id);
            return ResponseEntity.ok(album);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAlbum(
            @RequestParam("nome") String nome,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "copertina", required = false) MultipartFile copertina,
            @RequestParam(value = "foto", required = false) List<MultipartFile> foto) {

        try {
            // Crea album nel database
            Album album = new Album();
            album.setNome(nome);
            album.setDescrizione(descrizione);

            Integer albumId = albumService.createAlbum(album);
            System.out.println("✅ Album creato con ID: " + albumId);

            String albumFolderName = "album_" + albumId;
            String cloudinaryUrl = null;
            List<String> fotoUrls = new ArrayList<>();

            //Carica copertina su Cloudinary
            if (copertina != null && !copertina.isEmpty()) {
                cloudinaryUrl = cloudinaryService.uploadImage(copertina, albumFolderName);

                // Aggiorna album con URL Cloudinary
                album.setId(albumId);
                album.setFotoCopertina(cloudinaryUrl);
                albumService.updateAlbum(album);
            }

            //Carica foto su Cloudinary
            if (foto != null && !foto.isEmpty()) {
                for (MultipartFile file : foto) {
                    if (!file.isEmpty()) {
                        String fotoUrl = cloudinaryService.uploadImage(file, albumFolderName);
                        fotoUrls.add(fotoUrl);
                    }
                }

                // Salva URL nel database
                if (!fotoUrls.isEmpty()) {
                    List<Foto> fotoList = fotoUrls.stream()
                            .map(url -> new Foto(albumId, url))
                            .collect(Collectors.toList());
                    fotoDao.insertBatch(fotoList);
                    System.out.println("✅ " + fotoList.size() + " foto salvate su Cloudinary e DB");
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Album creato con successo su Cloudinary");
            response.put("id", albumId);
            response.put("folder", albumFolderName);
            response.put("numeroFoto", fotoUrls.size());
            response.put("cloudinary", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Errore durante l'upload: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateAlbum(
            @PathVariable Integer id,
            @RequestParam("nome") String nome,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "copertina", required = false) MultipartFile copertina) {

        try {
            Album album = new Album();
            album.setId(id);
            album.setNome(nome);
            album.setDescrizione(descrizione);

            // Aggiorna copertina su Cloudinary
            if (copertina != null && !copertina.isEmpty()) {
                String albumFolderName = "album_" + id;
                String cloudinaryUrl = cloudinaryService.uploadImage(copertina, albumFolderName);
                album.setFotoCopertina(cloudinaryUrl);
            }

            albumService.updateAlbum(album);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Album aggiornato con successo");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAlbum(@PathVariable Integer id) {
        try {
            // Ottieni tutte le foto per eliminarle da Cloudinary
            AlbumDTO album = albumService.getAlbumById(id);

            // Elimina copertina da Cloudinary
            if (album.getFotoCopertina() != null) {
                String publicId = cloudinaryService.extractPublicId(album.getFotoCopertina());
                if (publicId != null) {
                    cloudinaryService.deleteImage(publicId);
                }
            }

            // Elimina foto da Cloudinary
            List<Foto> foto = fotoDao.findByAlbumId(id);
            for (Foto f : foto) {
                String publicId = cloudinaryService.extractPublicId(f.getPercorso());
                if (publicId != null) {
                    cloudinaryService.deleteImage(publicId);
                }
            }

            // Elimina album dal database
            albumService.deleteAlbum(id);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Album eliminato con successo da Cloudinary e DB");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumDTO>> searchAlbums(@RequestParam String nome) {
        try {
            List<AlbumDTO> albums = albumService.searchAlbumsByName(nome);
            return ResponseEntity.ok(albums);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
