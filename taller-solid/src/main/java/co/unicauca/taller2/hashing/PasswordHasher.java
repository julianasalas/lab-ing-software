package co.unicauca.taller2.hashing;

/**
 * Abstracción para el cifrado y verificación de contraseñas.
 *
 * <p><b>Por qué existe esta interfaz (Principio de Inversión de
 * Dependencias - DIP):</b> las clases de alto nivel (como el servicio de
 * usuarios) no deben depender directamente de una librería concreta de
 * hashing (Argon2, BCrypt, etc.). En vez de eso, dependen de esta
 * abstracción. Así, si en el futuro se quiere cambiar Argon2 por otro
 * algoritmo, basta con crear una nueva implementación de esta interfaz,
 * sin tocar el resto del sistema (esto también respeta el Principio
 * Abierto/Cerrado - OCP).</p>
 */
public interface PasswordHasher {

    /**
     * Cifra una contraseña en texto plano y devuelve el hash resultante,
     * listo para ser almacenado en la base de datos.
     *
     * @param plainPassword contraseña en texto plano ingresada por el usuario
     * @return hash cifrado de la contraseña
     */
    String hash(char[] plainPassword);

    /**
     * Verifica si una contraseña en texto plano corresponde al hash
     * previamente almacenado.
     *
     * @param plainPassword  contraseña en texto plano ingresada por el usuario
     * @param hashedPassword hash almacenado en la base de datos
     * @return true si la contraseña coincide con el hash, false en caso contrario
     */
    boolean verify(char[] plainPassword, String hashedPassword);
}
