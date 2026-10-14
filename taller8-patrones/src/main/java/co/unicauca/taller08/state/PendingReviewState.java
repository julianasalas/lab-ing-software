package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * Pendiente de revisión: el contenido está completo y la pregunta espera
 * que se le asigne un revisor.
 */
public class PendingReviewState extends AbstractQuestionState {

    /** Se asigna un revisor → En revisión. */
    @Override
    public void assignReviewer(Question question, String reviewer) {
        if (reviewer == null || reviewer.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el nombre del revisor");
        }
        question.setReviewer(reviewer.trim());
        question.setState(new ReviewState());
    }

    @Override
    public String getName() {
        return "Pendiente de revisión";
    }
}
