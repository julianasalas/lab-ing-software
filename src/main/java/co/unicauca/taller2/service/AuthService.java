package co.unicauca.taller2.service;

import co.unicauca.taller2.hashing.PasswordHasher;
import co.unicauca.taller2.model.User;
import co.unicauca.taller2.model.UserStatus;
import co.unicauca.taller2.repository.UserRepository;

/**
 * Servicio de negocio encargado exclusivamente del inicio de sesión
 * (SRP: la responsabilidad de "autenticar" se separa de la
 * responsabilidad de "registrar/gestionar" usuarios, que vive en
 * {@link UserService}).
 *
 * Igual que UserService, depende de las abstracciones
 * {@link UserRepository} y {@link PasswordHasher}, no de implementaciones
 * concretas (DIP).
 */
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * Intenta autenticar a un usuario con su login y contraseña.
     *
     * @param username      login ingresado
     * @param plainPassword contraseña en texto plano ingresada
     * @return el usuario autenticado
     * @throws AuthenticationException si el usuario no existe, la
     *                                  contraseña es incorrecta o el
     *                                  usuario está inactivo
     */
    public User login(String username, String plainPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Usuario o contraseña incorrectos."));

        boolean passwordMatches = passwordHasher.verify(plainPassword.toCharArray(), user.getHashedPassword());
        if (!passwordMatches) {
            throw new AuthenticationException("Usuario o contraseña incorrectos.");
        }

        if (user.getStatus() == UserStatus.INACTIVO) {
            throw new AuthenticationException("El usuario '" + username + "' se encuentra inactivo. Contacte al administrador.");
        }

        return user;
    }
}
