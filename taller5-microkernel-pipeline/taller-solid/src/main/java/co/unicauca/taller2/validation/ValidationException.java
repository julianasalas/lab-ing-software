package co.unicauca.taller2.validation;

/**
 * Excepción lanzada cuando los datos de un usuario no cumplen las reglas
 * de negocio (por ejemplo, una contraseña que no cumple los requisitos
 * mínimos, o un username duplicado).
 *
 * Se usa una excepción de tipo "unchecked" (extiende RuntimeException)
 * para no obligar a todas las capas superiores a declarar "throws" en
 * cada método, manteniendo el código de la interfaz gráfica más limpio.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
