package co.unicauca.taller2.validation;

import java.util.regex.Pattern;

/**
 * Política de contraseñas exigida por la guía del taller:
 * <ul>
 *   <li>Mínimo 6 caracteres</li>
 *   <li>Al menos un dígito</li>
 *   <li>Al menos un carácter especial</li>
 *   <li>Al menos una mayúscula</li>
 * </ul>
 *
 * Esta clase es intercambiable por cualquier otra implementación de
 * {@link PasswordPolicy} sin romper el contrato esperado por quienes la
 * usan (Principio de Sustitución de Liskov - LSP).
 */
public class DefaultPasswordPolicy implements PasswordPolicy {

    private static final int MIN_LENGTH = 6;
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern UPPERCASE = Pattern.compile(".*[A-Z].*");
    // Cualquier carácter que no sea letra ni dígito se considera "especial"
    private static final Pattern SPECIAL_CHAR = Pattern.compile(".*[^a-zA-Z0-9].*");

    @Override
    public boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        return DIGIT.matcher(password).matches()
                && UPPERCASE.matcher(password).matches()
                && SPECIAL_CHAR.matcher(password).matches();
    }

    @Override
    public String getDescription() {
        return "La contraseña debe tener mínimo 6 caracteres, al menos un dígito, "
                + "al menos una mayúscula y al menos un carácter especial.";
    }
}
