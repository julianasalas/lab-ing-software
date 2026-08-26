package co.unicauca.taller2.model;

/**
 * Estado de un usuario dentro del sistema.
 * Un usuario INACTIVO no debe poder iniciar sesión, aunque sus datos
 * permanezcan almacenados en la base de datos.
 */
public enum UserStatus {
    ACTIVO,
    INACTIVO;

    public String toDisplayName() {
        return this == ACTIVO ? "Activo" : "Inactivo";
    }
}
