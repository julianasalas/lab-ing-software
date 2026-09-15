package co.unicauca.taller2.repository;

import co.unicauca.taller2.db.DatabaseConnection;
import co.unicauca.taller2.model.Role;
import co.unicauca.taller2.model.User;
import co.unicauca.taller2.model.UserStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación concreta de {@link UserRepository} que persiste los
 * usuarios en una base de datos SQLite, usando JDBC puro (sin ORM, tal
 * como lo pide el taller).
 *
 * Esta clase puede sustituirse en cualquier momento por otra
 * implementación de {@link UserRepository} (por ejemplo, una en memoria
 * para pruebas) sin alterar el comportamiento esperado por quien la
 * consume (Principio de Sustitución de Liskov - LSP), ya que respeta
 * fielmente el contrato definido por la interfaz.
 */
public class SQLiteUserRepository implements UserRepository {

    private final DatabaseConnection databaseConnection;

    /**
     * @param databaseConnection abstracción encargada de entregar la
     *                            conexión JDBC ya inicializada
     */
    public SQLiteUserRepository(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, full_name, role, status, hashed_password) VALUES (?, ?, ?, ?, ?)";
        Connection conn = databaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getFullName());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getStatus().name());
            ps.setString(5, user.getHashedPassword());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando el usuario en la base de datos", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = databaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando el usuario por username", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY full_name";
        Connection conn = databaseConnection.getConnection();
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException("Error consultando la lista de usuarios", e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET full_name = ?, role = ?, status = ?, hashed_password = ? WHERE id = ?";
        Connection conn = databaseConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getStatus().name());
            ps.setString(4, user.getHashedPassword());
            ps.setInt(5, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando el usuario", e);
        }
    }

    /**
     * Convierte una fila del ResultSet en un objeto User del dominio.
     * Centralizar este mapeo aquí evita duplicar la lógica de lectura en
     * cada método (SRP).
     */
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("full_name"),
                Role.valueOf(rs.getString("role")),
                UserStatus.valueOf(rs.getString("status")),
                rs.getString("hashed_password")
        );
    }
}
