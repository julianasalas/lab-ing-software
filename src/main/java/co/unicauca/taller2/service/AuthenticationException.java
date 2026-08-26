package co.unicauca.taller2.service;

/**
 * Excepción lanzada cuando el inicio de sesión falla: usuario inexistente,
 * contraseña incorrecta o usuario inactivo.
 *
 * Se usa un único tipo de excepción con distintos mensajes (en vez de
 * exponer si el error es "usuario no existe" o "contraseña incorrecta")
 * para no dar pistas a un posible atacante sobre cuál de los dos datos
 * fue el que falló.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
