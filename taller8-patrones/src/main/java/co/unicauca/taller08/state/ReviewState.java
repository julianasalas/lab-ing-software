package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * En revisión: el revisor analiza la pregunta y decide si la aprueba o la rechaza.
 */
public class ReviewState extends AbstractQuestionState {

    /** El revisor la aprueba → Aprobada. */
    @Override
    public void approve(Question question) {
        question.setState(new ApprovedState());
    }

    /** El revisor no la aprueba → Rechazada. */
    @Override
    public void reject(Question question, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo del rechazo");
        }
        question.setRejectionReason(reason.trim());
        question.setState(new RejectedState());
    }

    @Override
    public String getName() {
        return "En revisión";
    }
}
