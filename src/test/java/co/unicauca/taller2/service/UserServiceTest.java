package co.unicauca.taller2.service;

import co.unicauca.taller2.hashing.PasswordHasher;
import co.unicauca.taller2.model.Role;
import co.unicauca.taller2.model.User;
import co.unicauca.taller2.model.UserStatus;
import co.unicauca.taller2.repository.UserRepository;
import co.unicauca.taller2.validation.DefaultPasswordPolicy;
import co.unicauca.taller2.validation.UserValidator;
import co.unicauca.taller2.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para {@link UserService}.
 *
 * Gracias a que UserService depende de las abstracciones
 * {@link UserRepository} y {@link PasswordHasher} (DIP), estas pruebas
 * pueden reemplazar la base de datos real y el algoritmo de cifrado real
 * por dobles de prueba (mocks) con Mockito, haciendo que las pruebas sean
 * rápidas y no dependan de infraestructura externa.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserValidator userValidator = new UserValidator(new DefaultPasswordPolicy());
        userService = new UserService(userRepository, passwordHasher, userValidator);
    }

    @Test
    void deberiaRegistrarUnUsuarioValidoCorrectamente() {
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(passwordHasher.hash(any(char[].class))).thenReturn("hash-simulado");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1);
            return u;
        });

        User result = userService.registerUser("jdoe", "John Doe", Role.ESTUDIANTE, UserStatus.ACTIVO, "Clave1#");

        assertEquals(1, result.getId());
        assertEquals("jdoe", result.getUsername());
        assertEquals("hash-simulado", result.getHashedPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void noDeberiaRegistrarUnUsernameDuplicado() {
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(ValidationException.class, () ->
                userService.registerUser("jdoe", "John Doe", Role.ESTUDIANTE, UserStatus.ACTIVO, "Clave1#"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void noDeberiaRegistrarUnaContrasenaQueNoCumpleLaPolitica() {
        assertThrows(ValidationException.class, () ->
                userService.registerUser("jdoe", "John Doe", Role.ESTUDIANTE, UserStatus.ACTIVO, "123"));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordHasher, never()).hash(any(char[].class));
    }

    @Test
    void deberiaListarTodosLosUsuarios() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(
                new User(1, "jdoe", "John Doe", Role.ESTUDIANTE, UserStatus.ACTIVO, "hash")
        ));

        assertEquals(1, userService.listUsers().size());
    }

    @Test
    void deberiaCambiarElEstadoDeUnUsuarioExistente() {
        User existingUser = new User(1, "jdoe", "John Doe", Role.ESTUDIANTE, UserStatus.ACTIVO, "hash");
        when(userRepository.findByUsername("jdoe")).thenReturn(java.util.Optional.of(existingUser));

        userService.changeStatus("jdoe", UserStatus.INACTIVO);

        assertEquals(UserStatus.INACTIVO, existingUser.getStatus());
        verify(userRepository).update(existingUser);
    }

    @Test
    void deberiaLanzarExcepcionAlCambiarEstadoDeUsuarioInexistente() {
        when(userRepository.findByUsername("noexiste")).thenReturn(java.util.Optional.empty());

        assertThrows(ValidationException.class, () -> userService.changeStatus("noexiste", UserStatus.INACTIVO));
    }
}
