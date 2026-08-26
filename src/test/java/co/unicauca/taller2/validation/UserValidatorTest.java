package co.unicauca.taller2.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias para {@link UserValidator}.
 *
 * Se usa la implementación real {@link DefaultPasswordPolicy} como
 * colaborador porque es una clase simple y determinista; en un escenario
 * con reglas más complejas también sería válido reemplazarla por un mock.
 */
class UserValidatorTest {

    private UserValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserValidator(new DefaultPasswordPolicy());
    }

    @Test
    void deberiaAceptarUnUsernameValido() {
        assertDoesNotThrow(() -> validator.validateUsername("jdoe"));
    }

    @Test
    void deberiaRechazarUsernameVacio() {
        assertThrows(ValidationException.class, () -> validator.validateUsername("  "));
    }

    @Test
    void deberiaRechazarUsernameNulo() {
        assertThrows(ValidationException.class, () -> validator.validateUsername(null));
    }

    @Test
    void deberiaRechazarNombreCompletoVacio() {
        assertThrows(ValidationException.class, () -> validator.validateFullName(""));
    }

    @Test
    void deberiaAceptarContrasenaQueCumpleLaPolitica() {
        assertDoesNotThrow(() -> validator.validatePassword("Clave1#"));
    }

    @Test
    void deberiaRechazarContrasenaQueNoCumpleLaPolitica() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> validator.validatePassword("clave"));
        assertDoesNotThrow(() -> ex.getMessage()); // el mensaje debe existir para mostrarse en la UI
    }
}
