package domain;

/**
 * Tipos de pregunta que maneja el Banco de Preguntas.
 * Cada tipo tiene su propio Builder concreto.
 */
public enum QuestionType {
    MULTIPLE_CHOICE("Selección múltiple con única respuesta"),
    TRUE_FALSE("Verdadero / Falso"),
    OPEN("Abierta");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
