package it.unical.webdevelop.backafterglow.proxy;

import it.unical.webdevelop.backafterglow.dao.EventoDao;
import it.unical.webdevelop.backafterglow.model.Evento;
import it.unical.webdevelop.backafterglow.model.IscrizioneEvento;

import java.util.Optional;

public class IscrizioneEventoProxy extends IscrizioneEvento {

    private final EventoDao eventoDAO;
    private boolean eventoCaricato = false;

    public IscrizioneEventoProxy(EventoDao eventoDAO) {
        this.eventoDAO = eventoDAO;
    }

    @Override
    public Evento getEvento() {
        if (!eventoCaricato && super.getEventoId() != null) {
            Optional<Evento> evento = eventoDAO.findById(super.getEventoId());
            evento.ifPresent(super::setEvento);
            eventoCaricato = true;
        }
        return super.getEvento();
    }
}
