package domain;

import java.util.Objects;

/**
 * Opción de respuesta de una pregunta cerrada (selección múltiple o V/F).
 * Es inmutable: una vez creada no cambia.
 */
public final class Option {

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

    /** Letra que identifica la opción (A, B, C...). */
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Option)) {
            return false;
        }
        Option other = (Option) o;
        return correct == other.correct
                && label.equals(other.label)
                && text.equals(other.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, text, correct);
    }

    @Override
    public String toString() {
        return label + ") " + text + (correct ? "  <-- correcta" : "");
    }
}
