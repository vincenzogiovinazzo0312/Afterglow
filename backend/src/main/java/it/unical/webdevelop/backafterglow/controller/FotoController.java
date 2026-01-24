package it.unical.webdevelop.backafterglow.controller;

import it.unical.webdevelop.backafterglow.dto.FotoDTO;
import it.unical.webdevelop.backafterglow.dto.LikeDTO;
import it.unical.webdevelop.backafterglow.dto.CommentoDTO;
import it.unical.webdevelop.backafterglow.model.Foto;
import it.unical.webdevelop.backafterglow.services.FotoService;
import it.unical.webdevelop.backafterglow.services.CloudinaryService;
import it.unical.webdevelop.backafterglow.services.LikeService;
import it.unical.webdevelop.backafterglow.services.CommentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/foto")
@CrossOrigin(origins = "*")
public class FotoController {

    @Autowired
    private FotoService fotoService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentoService commentoService;

    // ========================================
    // ENDPOINTS FOTO ESISTENTI
    // ========================================

    //Ottieni tutte le foto
    @GetMapping
    public ResponseEntity<List<FotoDTO>> getAllFoto() {
        try {
            List<FotoDTO> foto = fotoService.getAllFoto();
            return ResponseEntity.ok(foto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //Ottieni foto da id
    @GetMapping("/{id}")
    public ResponseEntity<FotoDTO> getFoto(@PathVariable Integer id) {
        try {
            FotoDTO foto = fotoService.getFotoById(id);
            return ResponseEntity.ok(foto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //Ottieni tutte le foto di un album
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<FotoDTO>> getFotoByAlbum(@PathVariable Integer albumId) {
        try {
            List<FotoDTO> foto = fotoService.getFotoByAlbumId(albumId);
            return ResponseEntity.ok(foto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //Carica nuove foto su Cloudinary
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFoto(
            @RequestParam("idAlbum") Integer idAlbum,
            @RequestParam("foto") List<MultipartFile> foto) {

        try {
            String albumFolderName = "album_" + idAlbum;
            List<String> fotoUrls = new ArrayList<>();

            System.out.println("📸 Caricamento di " + foto.size() + " foto su Cloudinary...");

            int counter = 1;
            for (MultipartFile file : foto) {
                if (!file.isEmpty()) {
                    System.out.println("⬆️ Caricamento foto " + counter + "/" + foto.size() + "...");
                    String cloudinaryUrl = cloudinaryService.uploadImage(file, albumFolderName);
                    fotoUrls.add(cloudinaryUrl);
                    System.out.println("✅ Foto " + counter + " caricata: " + cloudinaryUrl);
                    counter++;
                }
            }

            if (!fotoUrls.isEmpty()) {
                List<Foto> fotoList = fotoUrls.stream()
                        .map(url -> new Foto(idAlbum, url))
                        .collect(Collectors.toList());

                fotoService.createFotoBatch(fotoList);
                System.out.println("✅ " + fotoList.size() + " foto salvate nel database");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Foto caricate con successo su Cloudinary");
            response.put("numeroFoto", fotoUrls.size());
            response.put("urls", fotoUrls);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Errore durante l'upload: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Aggiorna una foto (sostituisce l'immagine)
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateFoto(
            @PathVariable Integer id,
            @RequestParam("foto") MultipartFile nuovaFoto) {

        try {
            FotoDTO fotoEsistente = fotoService.getFotoById(id);

            String oldPublicId = cloudinaryService.extractPublicId(fotoEsistente.getPercorso());
            if (oldPublicId != null) {
                cloudinaryService.deleteImage(oldPublicId);
                System.out.println("🗑️ Vecchia foto eliminata da Cloudinary: " + oldPublicId);
            }

            String albumFolderName = "album_" + fotoEsistente.getIdAlbum();
            String nuovoUrl = cloudinaryService.uploadImage(nuovaFoto, albumFolderName);
            System.out.println("✅ Nuova foto caricata su Cloudinary: " + nuovoUrl);

            Foto foto = new Foto();
            foto.setIdFoto(id);
            foto.setIdAlbum(fotoEsistente.getIdAlbum());
            foto.setPercorso(nuovoUrl);
            fotoService.updateFoto(foto);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Foto aggiornata con successo");
            response.put("nuovoUrl", nuovoUrl);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    //Elimina una foto da Cloudinary e dal database
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFoto(@PathVariable Integer id) {
        try {
            FotoDTO foto = fotoService.getFotoById(id);
            System.out.println("🗑️ Eliminazione foto ID: " + id + " - URL: " + foto.getPercorso());

            String publicId = cloudinaryService.extractPublicId(foto.getPercorso());
            if (publicId != null) {
                cloudinaryService.deleteImage(publicId);
                System.out.println("✅ Foto eliminata da Cloudinary: " + publicId);
            } else {
                System.out.println("⚠️ Public ID non estratto, foto non eliminata da Cloudinary");
            }

            fotoService.deleteFoto(id);
            System.out.println("✅ Foto eliminata dal database");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Foto eliminata con successo da Cloudinary e database");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    //Elimina tutte le foto di un album
    @DeleteMapping("/album/{albumId}")
    public ResponseEntity<Map<String, String>> deleteFotoByAlbum(@PathVariable Integer albumId) {
        try {
            List<FotoDTO> fotoList = fotoService.getFotoByAlbumId(albumId);
            System.out.println("🗑️ Eliminazione di " + fotoList.size() + " foto dell'album " + albumId);

            int eliminateCloudinary = 0;
            for (FotoDTO foto : fotoList) {
                String publicId = cloudinaryService.extractPublicId(foto.getPercorso());
                if (publicId != null) {
                    cloudinaryService.deleteImage(publicId);
                    eliminateCloudinary++;
                }
            }

            fotoService.deleteFotoByAlbumId(albumId);

            System.out.println("✅ " + eliminateCloudinary + " foto eliminate da Cloudinary");
            System.out.println("✅ " + fotoList.size() + " foto eliminate dal database");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Tutte le foto dell'album eliminate con successo");
            response.put("numeroFoto", String.valueOf(fotoList.size()));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    //Conta le foto di un album
    @GetMapping("/count/{albumId}")
    public ResponseEntity<Map<String, Integer>> countFotoByAlbum(@PathVariable Integer albumId) {
        try {
            List<FotoDTO> foto = fotoService.getFotoByAlbumId(albumId);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", foto.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================
    //        LIKES (USA USERNAME)
    // ========================================

    //Ottieni info like (count + se utente ha messo like)
    @GetMapping("/{idFoto}/likes")
    public ResponseEntity<LikeDTO> getLikeInfo(
            @PathVariable Long idFoto,
            @RequestParam String username) {
        try {
            LikeDTO likeInfo = likeService.getLikeInfo(idFoto, username);
            return ResponseEntity.ok(likeInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // Aggiungi/Rimuovi like
    @PostMapping("/{idFoto}/likes/toggle")
    public ResponseEntity<LikeDTO> toggleLike(
            @PathVariable Long idFoto,
            @RequestParam String username) {
        try {
            LikeDTO result = likeService.toggleLike(idFoto, username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========================================
    //       COMMENTI (USA USERNAME)
    // ========================================

    //Ottieni tutti i commenti di una foto
    @GetMapping("/{idFoto}/commenti")
    public ResponseEntity<List<CommentoDTO>> getCommenti(@PathVariable Long idFoto) {
        try {
            List<CommentoDTO> commenti = commentoService.getCommentiByFoto(idFoto);
            return ResponseEntity.ok(commenti);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //Aggiungi un commento
    @PostMapping("/{idFoto}/commenti")
    public ResponseEntity<Map<String, String>> addCommento(
            @PathVariable Long idFoto,
            @RequestParam String username,
            @RequestParam String testo) {
        try {
            commentoService.addCommento(idFoto, username, testo);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Commento aggiunto con successo");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    //Elimina un commento
    @DeleteMapping("/commenti/{idCommento}")
    public ResponseEntity<Map<String, String>> deleteCommento(@PathVariable Long idCommento) {
        try {
            commentoService.deleteCommento(idCommento);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Commento eliminato con successo");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Errore interno del server");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    //Conta commenti di una foto
    @GetMapping("/{idFoto}/commenti/count")
    public ResponseEntity<Map<String, Integer>> getCommentCount(@PathVariable Long idFoto) {
        try {
            int count = commentoService.getCommentCount(idFoto);
            Map<String, Integer> response = new HashMap<>();
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
