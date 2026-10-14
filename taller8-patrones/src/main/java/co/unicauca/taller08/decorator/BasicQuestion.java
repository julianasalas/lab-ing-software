package co.unicauca.taller08.decorator;

import co.unicauca.taller08.domain.Question;

/**
 * Componente concreto: la pregunta sin características adicionales.
 * Vale 1.0 punto.
 */
public class BasicQuestion implements QuestionComponent {

    private final String description;

    public BasicQuestion(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        this.description = description.trim();
    }

    /** Crea el componente a partir de una pregunta del banco. */
    public BasicQuestion(Question question) {
        this(question.getStatement());
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getScore() {
        return 1.0;
    }
}
