package co.unicauca.taller2.repository;

import co.unicauca.taller2.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Abstracción para el acceso a los datos de los usuarios.
 *
 * <p><b>Principio de Inversión de Dependencias (DIP):</b> las capas de
 * alto nivel (por ejemplo {@link co.unicauca.taller2.service.UserService})
 * dependen de esta interfaz y NO de una implementación concreta como
 * {@link co.unicauca.taller2.repository.SQLiteUserRepository}. Esto permite:
 * <ul>
 *   <li>Cambiar el motor de base de datos en el futuro sin modificar la
 *       lógica de negocio (Principio Abierto/Cerrado - OCP).</li>
 *   <li>Probar la lógica de negocio con un repositorio "falso" (mock),
 *       sin necesidad de una base de datos real durante las pruebas
 *       unitarias.</li>
 * </ul>
 *
 * Esta interfaz sigue además el Principio de Segregación de Interfaces
 * (ISP): solo define las operaciones que la aplicación realmente necesita
 * sobre los usuarios (no se mezclan aquí, por ejemplo, operaciones de
 * reportes o de otras entidades ajenas a User).
 */
public interface UserRepository {

    /**
     * Guarda un nuevo usuario en el medio de almacenamiento.
     *
     * @param user usuario a guardar (sin id, se asigna automáticamente)
     * @return el mismo usuario con el id ya asignado
     */
    User save(User user);

    /**
     * Busca un usuario por su nombre de usuario (login).
     *
     * @param username login del usuario
     * @return el usuario envuelto en Optional, o Optional.empty() si no existe
     */
    Optional<User> findByUsername(String username);

    /**
     * @return la lista completa de usuarios registrados
     */
    List<User> findAll();

    /**
     * Indica si ya existe un usuario registrado con ese nombre de usuario.
     * Útil para validar duplicados antes de registrar.
     *
     * @param username login a verificar
     * @return true si el username ya está en uso
     */
    boolean existsByUsername(String username);

    /**
     * Actualiza los datos de un usuario ya existente.
     *
     * @param user usuario con los datos actualizados (debe tener id)
     */
    void update(User user);
}
