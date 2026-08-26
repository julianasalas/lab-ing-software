package co.unicauca.taller2.model;

/**
 * Enumeración que representa los roles disponibles para un usuario del
 * sistema. Cada rol determina qué opciones se muestran en el menú/tablero
 * una vez el usuario inicia sesión.
 *
 * Se usa un enum (en vez de un String suelto) para evitar valores inválidos
 * y para que el compilador nos ayude a detectar errores de escritura.
 */
public enum Role {
    ADMINISTRADOR,
    AUTOR_PREGUNTAS,
    REVISOR,
    DOCENTE,
    ESTUDIANTE;

    /**
     * Devuelve un nombre "bonito" para mostrar en la interfaz gráfica,
     * en vez del nombre técnico del enum (p. ej. AUTOR_PREGUNTAS -> "Autor de preguntas").
     *
     * @return texto legible para el usuario final
     */
    public String toDisplayName() {
        return switch (this) {
            case ADMINISTRADOR -> "Administrador";
            case AUTOR_PREGUNTAS -> "Autor de preguntas";
            case REVISOR -> "Revisor";
            case DOCENTE -> "Docente";
            case ESTUDIANTE -> "Estudiante";
        };
    }
}
