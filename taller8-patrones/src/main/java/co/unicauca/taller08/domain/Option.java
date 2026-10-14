package co.unicauca.taller08.domain;

/**
 * Opción de respuesta de una pregunta (A, B, C...).
 */
public class Option {

    private final String label;
    private final String text;
    private final boolean correct;

    public Option(String label, String text, boolean correct) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("La etiqueta de la opción es obligatoria");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("El texto de la opción es obligatorio");
        }
        this.label = label.trim();
        this.text = text.trim();
        this.correct = correct;
    }

    public String getLabel() {
        return label;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

    @Override
    public String toString() {
        return label + ") " + text + (correct ? "  (correcta)" : "");
    }
}
