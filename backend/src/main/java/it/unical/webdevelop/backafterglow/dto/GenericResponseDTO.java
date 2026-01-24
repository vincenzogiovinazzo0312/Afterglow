package it.unical.webdevelop.backafterglow.dto;


public class GenericResponseDTO {
    private boolean success;
    private String message;

    public GenericResponseDTO() {}

    public GenericResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
