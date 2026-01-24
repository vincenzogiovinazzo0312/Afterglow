package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.IscrizioneEventoDao;
import it.unical.webdevelop.backafterglow.dao.EventoDao;
import it.unical.webdevelop.backafterglow.dao.UserDao;
import it.unical.webdevelop.backafterglow.dto.IscrizioneEventoDTO;
import it.unical.webdevelop.backafterglow.dto.IscrizioneRapidaDTO;
import it.unical.webdevelop.backafterglow.model.IscrizioneEvento;
import it.unical.webdevelop.backafterglow.model.Evento;
import it.unical.webdevelop.backafterglow.model.Utente;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IscrizioneEventoService {

    private final IscrizioneEventoDao iscrizioneEventoDao;
    private final UserDao utenteDao;
    private final EventoDao eventoDao;

    public IscrizioneEventoService(IscrizioneEventoDao iscrizioneEventoDao,
                                   UserDao utenteDao,
                                   EventoDao eventoDao) {
        this.iscrizioneEventoDao = iscrizioneEventoDao;
        this.utenteDao = utenteDao;
        this.eventoDao = eventoDao;
    }

    public boolean creaIscrizione(IscrizioneEventoDTO dto) {
        if (iscrizioneEventoDao.existsByEventoIdAndTelefono(dto.getEventoId(), dto.getTelefono())) {
            throw new RuntimeException("Telefono già registrato per questo evento");
        }
        return iscrizioneEventoDao.insert(dto);
    }

    public boolean creaIscrizioneRapida(IscrizioneRapidaDTO dto, Integer utenteId) {
        // Recupera utente usando Optional
        Utente utente = utenteDao.findById(utenteId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // Costruisci DTO completo
        IscrizioneEventoDTO iscrizione = new IscrizioneEventoDTO();
        iscrizione.setEventoId(dto.getEventoId());
        iscrizione.setUtenteId(utenteId);
        iscrizione.setNome(utente.getNome());
        iscrizione.setCognome(utente.getCognome());
        iscrizione.setTelefono(utente.getTelefono());

        // Controllo duplicato
        if (iscrizioneEventoDao.existsByEventoIdAndUtenteId(dto.getEventoId(), utenteId)) {
            throw new RuntimeException("Utente già iscritto a questo evento");
        }

        return iscrizioneEventoDao.insert(iscrizione);
    }

    public List<IscrizioneEvento> getIscrizioniByEvento(Long eventoId) {
        return iscrizioneEventoDao.findByEventoId(eventoId);
    }

    public int contaIscrizioni(Long eventoId) {
        return iscrizioneEventoDao.countByEventoId(eventoId);
    }

    public boolean verificaIscrizioneConsentita(Long eventoId) {
        // Recupera l'evento
        Evento evento = eventoDao.findById(eventoId)
                .orElse(null);

        if (evento == null || evento.getData() == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dataEvento = evento.getData();

        // Calcola 48 ore prima e 5 ore prima dell'evento
        LocalDateTime apertura = dataEvento.minusHours(48);
        LocalDateTime chiusura = dataEvento.minusHours(5);

        // Iscrizione consentita solo se siamo dentro la finestra
        return now.isAfter(apertura) && now.isBefore(chiusura);
    }

    public boolean verificaUtenteIscritto(Long eventoId, Integer utenteId) {
        return iscrizioneEventoDao.existsByEventoIdAndUtenteId(eventoId, utenteId);
    }

    public boolean deleteIscrizione(Long id) {
        return iscrizioneEventoDao.deleteById(id);
    }

    public boolean aggiornaStatoEntrato(Long iscrizioneId, boolean entrato) {
        return iscrizioneEventoDao.updateEntrato(iscrizioneId, entrato);
    }
}
