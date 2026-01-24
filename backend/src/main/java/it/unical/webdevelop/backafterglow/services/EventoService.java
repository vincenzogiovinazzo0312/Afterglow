package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.EventoDao;
import it.unical.webdevelop.backafterglow.dto.EventoDTO;
import it.unical.webdevelop.backafterglow.model.Evento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoDao eventoDAO;
    private final CloudinaryService cloudinaryService;

    public EventoService(EventoDao eventoDAO, CloudinaryService cloudinaryService) {
        this.eventoDAO = eventoDAO;
        this.cloudinaryService = cloudinaryService;
    }

    private EventoDTO convertToDTO(Evento evento) {
        return new EventoDTO(
                evento.getId(),
                evento.getTitolo(),
                evento.getDescrizione(),
                evento.getData(),
                evento.getImmagine()
        );
    }

    // ===== LETTURA =====
    public List<EventoDTO> getEventiProssimi() {
        return eventoDAO.findEventiProssimi()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> getAllEventi() {
        return eventoDAO.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventoDTO> getEventoById(Long id) {
        return eventoDAO.findById(id).map(this::convertToDTO);
    }

    // ===== SCRITTURA =====
    @Transactional
    public boolean createEvento(EventoDTO eventoDTO) {
        return eventoDAO.insert(eventoDTO);
    }

    @Transactional
    public boolean updateEvento(EventoDTO eventoDTO) {
        return eventoDAO.update(eventoDTO);
    }

    @Transactional
    public boolean deleteEvento(Long id) {
        return eventoDAO.deleteById(id);
    }

    // ===== UPLOAD IMMAGINE EVENTO (Cloudinary) =====
    public String uploadImmagineEvento(MultipartFile file) throws IOException {
        return cloudinaryService.uploadImage(file, "eventi");
    }
}
