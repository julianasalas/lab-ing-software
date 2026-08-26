package co.unicauca.taller2.model;

import java.util.Objects;

/**
 * Entidad de dominio que representa a un usuario del sistema.
 *
 * Esta clase es intencionalmente "tonta" (no contiene lógica de negocio,
 * de validación ni de acceso a datos): solo agrupa los atributos de un
 * usuario. Esto respeta el Principio de Responsabilidad Única (SRP):
 * la responsabilidad de esta clase es únicamente representar los datos
 * de un usuario, no validarlos ni guardarlos.
 *
 * La contraseña que se almacena aquí SIEMPRE debe ser el hash ya cifrado
 * (nunca la contraseña en texto plano), para eso se apoya en la interfaz
 * {@link co.unicauca.taller2.hashing.PasswordHasher}.
 */
public class User {

    private Integer id; // null hasta que el usuario se persiste en la BD
    private String username;      // login
    private String fullName;      // nombre completo
    private Role role;
    private UserStatus status;
    private String hashedPassword; // contraseña ya cifrada, nunca en texto plano

    /**
     * Constructor para un usuario nuevo que todavía no tiene id asignado
     * (aún no ha sido guardado en la base de datos).
     */
    public User(String username, String fullName, Role role, UserStatus status, String hashedPassword) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Constructor completo, usado típicamente al reconstruir un usuario
     * a partir de una fila de la base de datos (donde ya existe un id).
     */
    public User(Integer id, String username, String fullName, Role role, UserStatus status, String hashedPassword) {
        this(username, fullName, role, status, hashedPassword);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                ", status=" + status +
                '}';
    }
}
