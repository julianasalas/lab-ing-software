package co.unicauca.taller2;

import co.unicauca.taller2.db.DatabaseConnection;
import co.unicauca.taller2.hashing.Argon2PasswordHasher;
import co.unicauca.taller2.hashing.PasswordHasher;
import co.unicauca.taller2.repository.SQLiteUserRepository;
import co.unicauca.taller2.repository.UserRepository;
import co.unicauca.taller2.service.AuthService;
import co.unicauca.taller2.service.UserService;
import co.unicauca.taller2.ui.DefaultRoleMenuProvider;
import co.unicauca.taller2.ui.LoginFrame;
import co.unicauca.taller2.ui.RoleMenuProvider;
import co.unicauca.taller2.validation.DefaultPasswordPolicy;
import co.unicauca.taller2.validation.PasswordPolicy;
import co.unicauca.taller2.validation.UserValidator;

import javax.swing.*;

/**
 * Victor Manuel Ortiz Rojas
 * Juliana Salas
 * Punto de entrada de la aplicación.
 *
 * <p>Esta clase actúa como "composition root": es el ÚNICO lugar del
 * proyecto donde se instancian las implementaciones concretas
 * ({@code SQLiteUserRepository}, {@code Argon2PasswordHasher}, etc.) y se
 * "inyectan" en las clases de negocio a través de sus constructores.</p>
 *
 * <p>Este patrón es precisamente el que se trabajó en el ejemplo 5 de
 * Inversión de Dependencias: las clases de negocio (services) y de
 * validación no crean sus propias dependencias con "new" en su interior;
 * en vez de eso, las reciben ya construidas desde afuera. Gracias a esto,
 * si se quiere cambiar SQLite por otra base de datos, o Argon2 por otro
 * algoritmo de hashing, el único archivo que debería tocarse es este.</p>
 */
public class Main {

    public static void main(String[] args) {
        // 1. Infraestructura: conexión a la base de datos SQLite
        DatabaseConnection databaseConnection = new DatabaseConnection();

        // 2. Implementaciones concretas de las abstracciones (DIP)
        UserRepository userRepository = new SQLiteUserRepository(databaseConnection);
        PasswordHasher passwordHasher = new Argon2PasswordHasher();
        PasswordPolicy passwordPolicy = new DefaultPasswordPolicy();
        UserValidator userValidator = new UserValidator(passwordPolicy);
        RoleMenuProvider roleMenuProvider = new DefaultRoleMenuProvider();

        // 3. Servicios de negocio, construidos a partir de las abstracciones
        UserService userService = new UserService(userRepository, passwordHasher, userValidator);
        AuthService authService = new AuthService(userRepository, passwordHasher);

        // 4. Lanzar la interfaz gráfica en el Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authService, userService, roleMenuProvider);
            loginFrame.setVisible(true);
        });

        // Cerrar la conexión a la base de datos al finalizar la aplicación
        Runtime.getRuntime().addShutdownHook(new Thread(databaseConnection::close));
    }
}
