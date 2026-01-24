package it.unical.webdevelop.backafterglow.proxy;

import it.unical.webdevelop.backafterglow.dao.IscrizioneEventoDao;
import it.unical.webdevelop.backafterglow.model.Evento;
import it.unical.webdevelop.backafterglow.model.IscrizioneEvento;

import java.util.List;

public class EventoProxy extends Evento {

    private final IscrizioneEventoDao iscrizioneEventoDAO;
    private boolean iscrizioniCaricate = false;

    public EventoProxy(IscrizioneEventoDao iscrizioneEventoDAO) {
        this.iscrizioneEventoDAO = iscrizioneEventoDAO;
    }

    @Override
    public List<IscrizioneEvento> getIscrizioni() {
        if (!iscrizioniCaricate && super.getId() != null) {
            List<IscrizioneEvento> iscrizioni = iscrizioneEventoDAO.findByEventoId(super.getId());
            super.setIscrizioni(iscrizioni);
            iscrizioniCaricate = true;
        }
        return super.getIscrizioni();
    }
}
