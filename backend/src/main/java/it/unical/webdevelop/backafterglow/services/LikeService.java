package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.LikeDao;
import it.unical.webdevelop.backafterglow.dto.LikeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private LikeDao likeDao;

    public int getLikeCount(Long idFoto) {
        return likeDao.countLikesByFoto(idFoto);
    }

    public boolean hasUserLiked(Long idFoto, String username) {
        return likeDao.hasUserLiked(idFoto, username);
    }

    @Transactional
    public LikeDTO toggleLike(Long idFoto, String username) {
        boolean wasLiked = likeDao.hasUserLiked(idFoto, username);

        if (wasLiked) {
            likeDao.removeLike(idFoto, username);
        } else {
            likeDao.addLike(idFoto, username);
        }

        int newCount = likeDao.countLikesByFoto(idFoto);
        boolean isLiked = !wasLiked;

        return new LikeDTO(idFoto, username, isLiked, newCount);
    }

    public LikeDTO getLikeInfo(Long idFoto, String username) {
        int count = likeDao.countLikesByFoto(idFoto);
        boolean liked = likeDao.hasUserLiked(idFoto, username);
        return new LikeDTO(idFoto, username, liked, count);
    }
}
