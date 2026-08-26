package co.unicauca.taller2.hashing;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Implementación concreta de {@link PasswordHasher} que utiliza el
 * algoritmo Argon2 (ganador del "Password Hashing Competition"), a través
 * de la librería argon2-jvm.
 *
 * Esta clase es intercambiable por cualquier otra implementación de
 * {@link PasswordHasher} sin que el resto de la aplicación lo note
 * (Principio de Sustitución de Liskov - LSP): en cualquier lugar donde se
 * espere un PasswordHasher, esta clase puede usarse sin romper el
 * comportamiento esperado.
 */
public class Argon2PasswordHasher implements PasswordHasher {

    // Parámetros recomendados para Argon2id (equilibrio entre seguridad y rendimiento)
    private static final int ITERATIONS = 4;
    private static final int MEMORY_KB = 65536; // 64 MB
    private static final int PARALLELISM = 2;

    private final Argon2 argon2;

    public Argon2PasswordHasher() {
        // Argon2id combina resistencia a ataques de canal lateral (side-channel)
        // y resistencia a ataques por GPU/ASIC.
        this.argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    }

    @Override
    public String hash(char[] plainPassword) {
        try {
            return argon2.hash(ITERATIONS, MEMORY_KB, PARALLELISM, plainPassword);
        } finally {
            // Buenas prácticas: limpiar el arreglo de caracteres en memoria
            // una vez que ya no se necesita, para reducir el tiempo que la
            // contraseña en texto plano permanece en memoria.
            argon2.wipeArray(plainPassword);
        }
    }

    @Override
    public boolean verify(char[] plainPassword, String hashedPassword) {
        try {
            return argon2.verify(hashedPassword, plainPassword);
        } finally {
            argon2.wipeArray(plainPassword);
        }
    }
}
