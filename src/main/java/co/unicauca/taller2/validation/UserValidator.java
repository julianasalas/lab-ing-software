package co.unicauca.taller2.validation;

/**
 * Encargado exclusivamente de validar los datos ingresados al registrar
 * un usuario (SRP: esta clase no guarda nada en base de datos ni cifra
 * contraseñas, solo valida).
 *
 * Depende de la abstracción {@link PasswordPolicy} y no de una regla de
 * contraseñas concreta, siguiendo el Principio de Inversión de
 * Dependencias (DIP): la política específica se inyecta por el
 * constructor, por lo que este validador funciona con cualquier política
 * que se le entregue.
 */
public class UserValidator {

    private final PasswordPolicy passwordPolicy;

    public UserValidator(PasswordPolicy passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    /**
     * Valida el nombre de usuario (login).
     *
     * @throws ValidationException si el username es inválido
     */
    public void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("El nombre de usuario (login) es obligatorio.");
        }
    }

    /**
     * Valida el nombre completo.
     *
     * @throws ValidationException si el nombre completo es inválido
     */
    public void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("El nombre completo es obligatorio.");
        }
    }

    /**
     * Valida la contraseña en texto plano contra la política configurada.
     *
     * @throws ValidationException si la contraseña no cumple la política
     */
    public void validatePassword(String plainPassword) {
        if (!passwordPolicy.isValid(plainPassword)) {
            throw new ValidationException(passwordPolicy.getDescription());
        }
    }
}
