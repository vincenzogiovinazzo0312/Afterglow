package it.unical.webdevelop.backafterglow.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hash della password (da usare nella registrazione)
    public String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // Controllo della password (da usare nel login)
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
