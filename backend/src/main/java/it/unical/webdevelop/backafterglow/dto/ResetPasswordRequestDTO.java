package it.unical.webdevelop.backafterglow.dto;

public class ResetPasswordRequestDTO {
    private String username;
    private String otp;
    private String newPassword;

    // Getters e Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
