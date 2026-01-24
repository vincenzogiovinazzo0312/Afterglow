package it.unical.webdevelop.backafterglow.dto;

public class LikeDTO {
    private Long idFoto;
    private String username;  // ✅ Cambiato da idUtente
    private boolean liked;
    private int count;

    public LikeDTO() {}

    public LikeDTO(Long idFoto, String username, boolean liked, int count) {
        this.idFoto = idFoto;
        this.username = username;
        this.liked = liked;
        this.count = count;
    }

    // Getters e Setters
    public Long getIdFoto() { return idFoto; }
    public void setIdFoto(Long idFoto) { this.idFoto = idFoto; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
