package it.unical.webdevelop.backafterglow.dto;

public class ForgotPasswordResponseDTO {
    private boolean success;
    private String message;
    private String otp;
    private String time;
    private String email;

    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
