package co.unicauca.taller2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Responsable únicamente de abrir la conexión a la base de datos SQLite
 * y de asegurar que la tabla de usuarios exista (SRP: esta clase no sabe
 * nada de usuarios, contraseñas ni validaciones; solo maneja la conexión
 * JDBC).
 *
 * Por defecto se usa una base de datos en archivo físico
 * ("usuarios.db" en el directorio de ejecución), pero también se puede
 * usar una base de datos en memoria (útil para pruebas), pasando la URL
 * "jdbc:sqlite::memory:" al constructor.
 */
public class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:sqlite:usuarios.db";

    private final String jdbcUrl;
    private Connection connection;

    public DatabaseConnection() {
        this(DEFAULT_URL);
    }

    public DatabaseConnection(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Abre (o reutiliza) la conexión con la base de datos y crea la tabla
     * de usuarios si todavía no existe.
     *
     * @return la conexión JDBC lista para usar
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(jdbcUrl);
                initSchema(connection);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("No fue posible conectar a la base de datos SQLite", e);
        }
    }

    /**
     * Crea la tabla "users" si no existe todavía. Se ejecuta automáticamente
     * la primera vez que se abre la conexión.
     */
    private void initSchema(Connection conn) throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    full_name TEXT NOT NULL,
                    role TEXT NOT NULL,
                    status TEXT NOT NULL,
                    hashed_password TEXT NOT NULL
                );
                """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cerrando la conexión a la base de datos", e);
        }
    }
}
