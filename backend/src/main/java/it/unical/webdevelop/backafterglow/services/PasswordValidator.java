package it.unical.webdevelop.backafterglow.services;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errors.add("La password non può essere vuota");
            return new ValidationResult(false, errors);
        }

        if (password.length() < MIN_LENGTH) {
            errors.add("La password deve contenere almeno " + MIN_LENGTH + " caratteri");
        }

        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("La password deve contenere almeno una lettera maiuscola");
        }

        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("La password deve contenere almeno una lettera minuscola");
        }

        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.add("La password deve contenere almeno un numero");
        }

        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("La password deve contenere almeno un carattere speciale (!@#$%^&*(),.?\":{}|<>)");
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
