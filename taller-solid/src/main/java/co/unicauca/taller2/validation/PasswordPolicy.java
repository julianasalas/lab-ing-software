package co.unicauca.taller2.validation;

/**
 * Abstracción para la política de complejidad de contraseñas.
 *
 * <p>Se separa la "regla" (qué hace válida a una contraseña) de quien la
 * usa ({@link UserValidator}), siguiendo el mismo espíritu del ejemplo de
 * Inversión de Dependencias visto en teoría: si mañana la política de
 * contraseñas cambia (por ejemplo, se exige una longitud mínima de 8 en
 * vez de 6), basta con crear una nueva implementación de esta interfaz
 * sin modificar el validador ni el servicio de usuarios (OCP + DIP).</p>
 */
public interface PasswordPolicy {

    /**
     * Verifica si una contraseña cumple la política de complejidad.
     *
     * @param password contraseña en texto plano a evaluar
     * @return true si la contraseña es válida según la política
     */
    boolean isValid(String password);

    /**
     * @return una descripción legible de la política, útil para mostrar
     *         mensajes de error al usuario.
     */
    String getDescription();
}
