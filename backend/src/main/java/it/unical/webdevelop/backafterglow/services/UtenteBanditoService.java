package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.UtenteBanditoDao;
import it.unical.webdevelop.backafterglow.dto.UtenteBanditoDTO;
import it.unical.webdevelop.backafterglow.proxy.UtenteBanditoProxy;
import it.unical.webdevelop.backafterglow.model.Utente;
import it.unical.webdevelop.backafterglow.model.UtenteBandito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UtenteBanditoService {

    @Autowired
    private UtenteBanditoDao utenteBanditoDao;

    // ============= CONVERSIONI DTO COMPLETO =============

    private UtenteBanditoDTO convertToFullDTO(Utente utente) {
        return new UtenteBanditoDTO(
                utente.getId(),
                utente.getUsername(),
                utente.getNome(),
                utente.getCognome(),
                utente.getTelefono(),
                utente.getEmail(),
                utente.getRuolo()
        );
    }

    private UtenteBanditoDTO convertBanditoToFullDTO(UtenteBandito utenteBandito) {
        return new UtenteBanditoDTO(
                utenteBandito.getId(),
                utenteBandito.getUsername(),
                utenteBandito.getNome(),
                utenteBandito.getCognome(),
                utenteBandito.getTelefono(),
                utenteBandito.getEmail(),
                utenteBandito.getRuolo()
        );
    }

    // ============= CONVERSIONI DTO PROXY =============

    private UtenteBanditoProxy convertToProxyDTO(Utente utente) {
        return new UtenteBanditoProxy(
                utente.getId(),
                utente.getUsername(),
                utente.getNome(),
                utente.getCognome()
        );
    }

    private UtenteBanditoProxy convertBanditoToProxyDTO(UtenteBandito utenteBandito) {
        return new UtenteBanditoProxy(
                utenteBandito.getId(),
                utenteBandito.getUsername(),
                utenteBandito.getNome(),
                utenteBandito.getCognome()
        );
    }

    // ============= METODI CON DTO PROXY  =============

    public List<UtenteBanditoProxy> getUtentiNonBanditi() {
        return utenteBanditoDao.findUtentiNonBanditi()
                .stream()
                .map(this::convertToProxyDTO)
                .collect(Collectors.toList());
    }

    public List<UtenteBanditoProxy> getUtentiBanditi() {
        return utenteBanditoDao.findUtentiBanditi()
                .stream()
                .map(this::convertBanditoToProxyDTO)
                .collect(Collectors.toList());
    }

    public List<UtenteBanditoProxy> cercaUtenti(String nome, String cognome) {
        List<UtenteBanditoProxy> risultati = new ArrayList<>();

        List<UtenteBanditoProxy> nonBanditi = utenteBanditoDao.cercaUtentiNonBanditi(nome, cognome)
                .stream()
                .map(this::convertToProxyDTO)
                .collect(Collectors.toList());

        List<UtenteBanditoProxy> banditi = utenteBanditoDao.cercaUtentiBanditi(nome, cognome)
                .stream()
                .map(this::convertBanditoToProxyDTO)
                .collect(Collectors.toList());

        risultati.addAll(nonBanditi);
        risultati.addAll(banditi);

        return risultati;
    }

    // ============= METODI CON DTO COMPLETO (per uso interno/admin) =============

    public List<UtenteBanditoDTO> getUtentiNonBanditiCompleto() {
        return utenteBanditoDao.findUtentiNonBanditi()
                .stream()
                .map(this::convertToFullDTO)
                .collect(Collectors.toList());
    }

    public List<UtenteBanditoDTO> getUtentiBanditiCompleto() {
        return utenteBanditoDao.findUtentiBanditi()
                .stream()
                .map(this::convertBanditoToFullDTO)
                .collect(Collectors.toList());
    }

    public UtenteBanditoDTO getUtenteByIdCompleto(Integer id) {
        // Cerca prima in utenti
        List<Utente> utenti = utenteBanditoDao.findUtentiNonBanditi();
        for (Utente u : utenti) {
            if (u.getId().equals(id)) {
                return convertToFullDTO(u);
            }
        }

        // Se non trovato, cerca in banditi
        List<UtenteBandito> banditi = utenteBanditoDao.findUtentiBanditi();
        for (UtenteBandito ub : banditi) {
            if (ub.getId().equals(id)) {
                return convertBanditoToFullDTO(ub);
            }
        }

        throw new RuntimeException("Utente non trovato con id: " + id);
    }

    // ============= OPERAZIONI BANDISCI/RIPRISTINA =============

    @Transactional
    public void bandisciUtente(Integer id) {
        if (utenteBanditoDao.isUtenteBandito(id)) {
            throw new RuntimeException("Utente già bandito");
        }
        utenteBanditoDao.bandisciUtente(id);
    }

    @Transactional
    public void ripristinaUtente(Integer id) {
        if (!utenteBanditoDao.isUtenteBandito(id)) {
            throw new RuntimeException("Utente non è bandito");
        }
        utenteBanditoDao.ripristinaUtente(id);
    }
}
