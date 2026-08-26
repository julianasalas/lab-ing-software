package co.unicauca.taller2.service;

import co.unicauca.taller2.hashing.PasswordHasher;
import co.unicauca.taller2.model.Role;
import co.unicauca.taller2.model.User;
import co.unicauca.taller2.model.UserStatus;
import co.unicauca.taller2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para {@link AuthService}, cubriendo los tres casos
 * relevantes del inicio de sesión: usuario inexistente, contraseña
 * incorrecta y usuario inactivo.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordHasher);
    }

    @Test
    void deberiaAutenticarUnUsuarioActivoConCredencialesCorrectas() {
        User user = new User(1, "jdoe", "John Doe", Role.DOCENTE, UserStatus.ACTIVO, "hash-almacenado");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(passwordHasher.verify(any(char[].class), eq("hash-almacenado"))).thenReturn(true);

        User result = authService.login("jdoe", "Clave1#");

        assertEquals("jdoe", result.getUsername());
    }

    @Test
    void deberiaRechazarUnUsuarioQueNoExiste() {
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authService.login("noexiste", "Clave1#"));
    }

    @Test
    void deberiaRechazarUnaContrasenaIncorrecta() {
        User user = new User(1, "jdoe", "John Doe", Role.DOCENTE, UserStatus.ACTIVO, "hash-almacenado");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(passwordHasher.verify(any(char[].class), eq("hash-almacenado"))).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authService.login("jdoe", "ClaveIncorrecta1#"));
    }

    @Test
    void deberiaRechazarUnUsuarioInactivo() {
        User user = new User(1, "jdoe", "John Doe", Role.DOCENTE, UserStatus.INACTIVO, "hash-almacenado");
        when(userRepository.findByUsername("jdoe")).thenReturn(Optional.of(user));
        when(passwordHasher.verify(any(char[].class), eq("hash-almacenado"))).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authService.login("jdoe", "Clave1#"));
    }
}
