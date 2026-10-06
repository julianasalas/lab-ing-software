package domain;

/**
 * Nivel de dificultad de una pregunta del Banco de Preguntas Saber Pro.
 */
public enum DifficultyLevel {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta");

    private final String displayName;

    DifficultyLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
