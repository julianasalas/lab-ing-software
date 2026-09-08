package co.unicauca.taller2.service;

import co.unicauca.taller2.hashing.PasswordHasher;
import co.unicauca.taller2.model.Role;
import co.unicauca.taller2.model.User;
import co.unicauca.taller2.model.UserStatus;
import co.unicauca.taller2.repository.UserRepository;
import co.unicauca.taller2.validation.UserValidator;
import co.unicauca.taller2.validation.ValidationException;

import java.util.List;

/**
 * Servicio de negocio encargado del registro y consulta de usuarios.
 *
 * <p><b>Principio de Inversión de Dependencias (DIP):</b> esta clase de
 * "alto nivel" no depende de clases concretas como
 * {@code SQLiteUserRepository} o {@code Argon2PasswordHasher}, sino de las
 * abstracciones {@link UserRepository} y {@link PasswordHasher}. Las
 * implementaciones concretas se inyectan por el constructor (inyección de
 * dependencias manual), tal como en el ejemplo 5 de la clase teórica.
 * Esto permite:</p>
 * <ul>
 *   <li>Cambiar la base de datos o el algoritmo de cifrado sin modificar
 *       esta clase (OCP).</li>
 *   <li>Probar esta clase con dobles de prueba (mocks) en las pruebas
 *       unitarias, sin necesidad de una base de datos real.</li>
 * </ul>
 */
public class UserService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final UserValidator userValidator;

    public UserService(UserRepository userRepository, PasswordHasher passwordHasher, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.userValidator = userValidator;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param username      login del usuario
     * @param fullName      nombre completo
     * @param role          rol asignado
     * @param status        estado inicial (Activo/Inactivo)
     * @param plainPassword contraseña en texto plano (se cifra antes de guardar)
     * @return el usuario ya persistido, con su id asignado
     * @throws ValidationException si algún dato no cumple las reglas de negocio
     */
    public User registerUser(String username, String fullName, Role role, UserStatus status, String plainPassword) {
        userValidator.validateUsername(username);
        userValidator.validateFullName(fullName);
        userValidator.validatePassword(plainPassword);

        if (userRepository.existsByUsername(username)) {
            throw new ValidationException("Ya existe un usuario registrado con el login '" + username + "'.");
        }

        String hashedPassword = passwordHasher.hash(plainPassword.toCharArray());
        User newUser = new User(username, fullName, role, status, hashedPassword);
        return userRepository.save(newUser);
    }

    /**
     * @return la lista completa de usuarios registrados en el sistema
     */
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    /**
     * Activa o inactiva un usuario existente.
     *
     * @param username     login del usuario a modificar
     * @param newStatus    nuevo estado
     */
    public void changeStatus(String username, UserStatus newStatus) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ValidationException("No existe un usuario con el login '" + username + "'."));
        user.setStatus(newStatus);
        userRepository.update(user);
    }
}
