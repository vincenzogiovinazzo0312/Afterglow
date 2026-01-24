package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.CommentoDao;
import it.unical.webdevelop.backafterglow.dto.CommentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentoService {

    @Autowired
    private CommentoDao commentoDao;

    public List<CommentoDTO> getCommentiByFoto(Long idFoto) {
        return commentoDao.getCommentiByFoto(idFoto);
    }

    @Transactional
    public void addCommento(Long idFoto, String username, String testo) {
        if (testo == null || testo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il commento non può essere vuoto");
        }
        if (testo.length() > 500) {
            throw new IllegalArgumentException("Il commento è troppo lungo (max 500 caratteri)");
        }
        commentoDao.addCommento(idFoto, username, testo);
    }

    @Transactional
    public void deleteCommento(Long idCommento) {
        commentoDao.deleteCommento(idCommento);
    }

    public int getCommentCount(Long idFoto) {
        return commentoDao.countCommentiByFoto(idFoto);
    }
}
