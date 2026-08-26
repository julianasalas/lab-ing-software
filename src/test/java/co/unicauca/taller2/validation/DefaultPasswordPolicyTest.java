package co.unicauca.taller2.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias para {@link DefaultPasswordPolicy}.
 * Se verifica cada regla exigida por la guía del taller de forma aislada.
 */
class DefaultPasswordPolicyTest {

    private final DefaultPasswordPolicy policy = new DefaultPasswordPolicy();

    @Test
    void deberiaAceptarUnaContrasenaQueCumpleTodasLasReglas() {
        assertTrue(policy.isValid("Clave1#"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ab1#", "A1#z"}) // menos de 6 caracteres
    void deberiaRechazarContrasenaMasCortaQueElMinimo(String password) {
        assertFalse(policy.isValid(password));
    }

    @Test
    void deberiaRechazarContrasenaSinDigito() {
        assertFalse(policy.isValid("Clave#abc"));
    }

    @Test
    void deberiaRechazarContrasenaSinMayuscula() {
        assertFalse(policy.isValid("clave1#abc"));
    }

    @Test
    void deberiaRechazarContrasenaSinCaracterEspecial() {
        assertFalse(policy.isValid("Clave123"));
    }

    @Test
    void deberiaRechazarContrasenaNula() {
        assertFalse(policy.isValid(null));
    }
}
