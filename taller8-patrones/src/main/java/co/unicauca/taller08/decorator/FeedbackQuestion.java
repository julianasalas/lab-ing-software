package co.unicauca.taller08.decorator;

/**
 * Pregunta con retroalimentación: agrega una explicación para el estudiante.
 */
public class FeedbackQuestion extends QuestionDecorator {

    private final String feedback;

    public FeedbackQuestion(QuestionComponent question, String feedback) {
        super(question);
        if (feedback == null || feedback.isBlank()) {
            throw new IllegalArgumentException("La retroalimentación es obligatoria");
        }
        this.feedback = feedback.trim();
    }

    @Override
    public String getDescription() {
        return question.getDescription() + " [RETROALIMENTACIÓN: " + feedback + "]";
    }
}
