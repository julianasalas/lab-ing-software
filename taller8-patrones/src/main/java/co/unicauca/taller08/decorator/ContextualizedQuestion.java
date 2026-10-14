package co.unicauca.taller08.decorator;

/**
 * Pregunta contextualizada: agrega un texto de contexto antes del
 * enunciado y suma 0.25 puntos por la lectura adicional.
 */
public class ContextualizedQuestion extends QuestionDecorator {

    public static final double EXTRA_SCORE = 0.25;

    private final String context;

    public ContextualizedQuestion(QuestionComponent question, String context) {
        super(question);
        if (context == null || context.isBlank()) {
            throw new IllegalArgumentException("El contexto es obligatorio");
        }
        this.context = context.trim();
    }

    @Override
    public String getDescription() {
        return "[CONTEXTO: " + context + "] " + question.getDescription();
    }

    @Override
    public double getScore() {
        return question.getScore() + EXTRA_SCORE;
    }
}
