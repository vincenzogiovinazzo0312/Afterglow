package it.unical.webdevelop.backafterglow.services;

import it.unical.webdevelop.backafterglow.dao.OtpCodeDao;
import it.unical.webdevelop.backafterglow.dao.UserDao;
import it.unical.webdevelop.backafterglow.dao.CredenzialiDao;
import it.unical.webdevelop.backafterglow.model.Utente;
import it.unical.webdevelop.backafterglow.dto.OtpCodeDTO;
import it.unical.webdevelop.backafterglow.dto.ForgotPasswordRequestDTO;
import it.unical.webdevelop.backafterglow.dto.ForgotPasswordResponseDTO;
import it.unical.webdevelop.backafterglow.dto.ResetPasswordRequestDTO;
import it.unical.webdevelop.backafterglow.dto.GenericResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private OtpCodeDao otpCodeDAO;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CredenzialiDao credenzialiDao;

    @Autowired
    private PasswordService passwordService;

    public ForgotPasswordResponseDTO generateAndSendOtp(ForgotPasswordRequestDTO requestDTO) {
        Utente utente = userDao.findByUsername(requestDTO.getUsername());
        if (utente == null) {
            throw new RuntimeException("Utente non trovato");
        }

        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpCodeDTO otpCodeDTO = new OtpCodeDTO();
        otpCodeDTO.setUsername(requestDTO.getUsername());
        otpCodeDTO.setOtpCode(otp);
        otpCodeDTO.setExpirationTime(LocalDateTime.now().plusMinutes(30));
        otpCodeDTO.setUsed(false);
        otpCodeDTO.setCreatedAt(LocalDateTime.now());

        otpCodeDAO.insert(otpCodeDTO);

        ForgotPasswordResponseDTO responseDTO = new ForgotPasswordResponseDTO();
        responseDTO.setSuccess(true);
        responseDTO.setMessage("OTP generato con successo");
        responseDTO.setOtp(otp);
        responseDTO.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        responseDTO.setEmail(utente.getEmail());

        return responseDTO;
    }

    public GenericResponseDTO resetPassword(ResetPasswordRequestDTO requestDTO) {
        OtpCodeDTO otpCode = otpCodeDAO.findValidOtp(
                requestDTO.getUsername(),
                requestDTO.getOtp()
        );

        if (otpCode == null) {
            return new GenericResponseDTO(false, "OTP non valido o scaduto");
        }

        otpCodeDAO.updateUsedStatus(otpCode.getId(), true);

        Utente utente = userDao.findByUsername(requestDTO.getUsername());

        String hashedPassword = passwordService.hashPassword(requestDTO.getNewPassword());
        credenzialiDao.updatePassword(utente.getId(), hashedPassword);

        return new GenericResponseDTO(true, "Password aggiornata con successo");
    }
}
