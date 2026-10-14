package co.unicauca.taller08.domain;

/**
 * Tipos de pregunta que maneja el Banco de Preguntas Saber PRO.
 * Para agregar un tipo nuevo se agrega aquí y se crea su evaluador
 * (Template Method), sin modificar los demás.
 */
public enum QuestionType {
    SINGLE_CHOICE("Selección múltiple con única respuesta"),
    MULTIPLE_CHOICE("Selección múltiple con múltiples respuestas"),
    TRUE_FALSE("Verdadero / Falso");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
