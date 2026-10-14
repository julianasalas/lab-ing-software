package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * Rechazada: el revisor no aprobó la pregunta. Vuelve a Borrador para
 * realizar los ajustes.
 */
public class RejectedState extends AbstractQuestionState {

    /** Rechazada → Borrador. */
    @Override
    public void returnToDraft(Question question) {
        question.setReviewer(null);
        question.setState(new DraftState());
    }

    @Override
    public String getName() {
        return "Rechazada";
    }
}
