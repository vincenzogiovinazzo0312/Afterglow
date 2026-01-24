package it.unical.webdevelop.backafterglow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.HashMap;

import it.unical.webdevelop.backafterglow.dao.UserDao;
import it.unical.webdevelop.backafterglow.dao.CredenzialiDao;
import it.unical.webdevelop.backafterglow.model.Utente;
import it.unical.webdevelop.backafterglow.model.Credenziali;
import it.unical.webdevelop.backafterglow.dto.LoginDTO;
import it.unical.webdevelop.backafterglow.dto.RegistrazioneDTO;
import it.unical.webdevelop.backafterglow.dto.ForgotPasswordRequestDTO;
import it.unical.webdevelop.backafterglow.dto.ForgotPasswordResponseDTO;
import it.unical.webdevelop.backafterglow.dto.ResetPasswordRequestDTO;
import it.unical.webdevelop.backafterglow.dto.GenericResponseDTO;
import it.unical.webdevelop.backafterglow.services.PasswordService;
import it.unical.webdevelop.backafterglow.services.PasswordResetService;
import it.unical.webdevelop.backafterglow.services.PasswordValidator;
import it.unical.webdevelop.backafterglow.security.JwtTokenProvider;
import it.unical.webdevelop.backafterglow.dao.UtenteBanditoDao;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CredenzialiDao credenzialiDao;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private PasswordValidator passwordValidator;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UtenteBanditoDao utenteBanditoDao;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            Utente utente = userDao.findByUsername(loginDTO.getUsername());

            boolean bandito= utenteBanditoDao.isBanditoUsername(loginDTO.getUsername());

            if(bandito){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utente bandito dal locale"));

            }

            if (utente == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Utente non trovato"));
            }

            Credenziali cred = credenzialiDao.findByIdUtente(utente.getId());

            if (cred == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Credenziali non trovate"));
            }

            boolean valido = passwordService.verifyPassword(loginDTO.getPassword(), cred.getPasswordHash());

            if (valido) {
                // Converti ruolo numerico in stringa
                String role = utente.getRuolo() == 0 ? "ADMIN" : "USER";

                //Genera il token con userId
                String token = jwtTokenProvider.generateToken(
                        utente.getUsername(),
                        role,
                        utente.getId()
                );

                // Risposta con token, ruolo, username e userId
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("role", role);
                response.put("username", utente.getUsername());
                response.put("userId", utente.getId());
                response.put("message", "Login OK");

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Password errata"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Errore durante il login: " + ex.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrazioneDTO dto) {
        try {
            // VALIDAZIONE PASSWORD
            PasswordValidator.ValidationResult validationResult = passwordValidator.validate(dto.getPassword());
            if (!validationResult.isValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", validationResult.getErrorMessage()));
            }

            boolean bandito=false;

            if(utenteBanditoDao.isBanditoUsername(dto.getUsername())|| utenteBanditoDao.isBanditoEmail(dto.getEmail())|| utenteBanditoDao.isBanditoTelefono(dto.getTelefono())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "Uno dei dati che ha inserito corrisponde ad un utente bandito dal nostro locale"));
            }


            Utente utente = new Utente();
            utente.setUsername(dto.getUsername());
            utente.setNome(dto.getNome());
            utente.setCognome(dto.getCognome());
            utente.setTelefono(dto.getTelefono());
            utente.setEmail(dto.getEmail());
            utente.setRuolo(dto.getRuolo() != null ? dto.getRuolo() : 1); // Default: USER (1)

            int rowsAffected = userDao.insert(utente);

            if (rowsAffected > 0) {
                Utente utenteInserito = userDao.findByUsername(dto.getUsername());

                Credenziali credenziali = new Credenziali();
                credenziali.setIdUtente(utenteInserito.getId());
                credenziali.setPasswordHash(passwordService.hashPassword(dto.getPassword()));

                credenzialiDao.insert(credenziali);

                return ResponseEntity.ok(Map.of(
                        "message", "Registrazione OK",
                        "userId", utenteInserito.getId()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Errore durante l'inserimento"));
            }

        } catch (DataIntegrityViolationException ex) {
            String msg = ex.getMostSpecificCause().getMessage();
            if (msg != null && msg.contains("utenti_email_key")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email già esistente!"));
            } else if (msg != null && msg.contains("utenti_username_key")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Username già esistente!"));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Dati duplicati."));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Errore di sistema: " + ex.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO requestDTO) {
        try {
            ForgotPasswordResponseDTO responseDTO =
                    passwordResetService.generateAndSendOtp(requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            ForgotPasswordResponseDTO errorResponse = new ForgotPasswordResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponseDTO> resetPassword(
            @RequestBody ResetPasswordRequestDTO requestDTO) {
        try {
            // VALIDAZIONE PASSWORD
            PasswordValidator.ValidationResult validationResult =
                    passwordValidator.validate(requestDTO.getNewPassword());

            if (!validationResult.isValid()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new GenericResponseDTO(false, validationResult.getErrorMessage()));
            }

            GenericResponseDTO responseDTO =
                    passwordResetService.resetPassword(requestDTO);

            if (responseDTO.isSuccess()) {
                return ResponseEntity.ok(responseDTO);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new GenericResponseDTO(false, e.getMessage()));
        }
    }
}
