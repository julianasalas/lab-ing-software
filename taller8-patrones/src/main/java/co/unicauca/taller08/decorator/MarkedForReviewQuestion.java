package co.unicauca.taller08.decorator;

/**
 * Pregunta marcada para revisión.
 */
public class MarkedForReviewQuestion extends QuestionDecorator {

    public MarkedForReviewQuestion(QuestionComponent question) {
        super(question);
    }

    @Override
    public String getDescription() {
        return question.getDescription() + " [MARCADA PARA REVISIÓN]";
    }
}
