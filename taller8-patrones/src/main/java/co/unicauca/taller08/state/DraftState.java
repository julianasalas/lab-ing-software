package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.service.QuestionValidator;

/**
 * Borrador: la pregunta se crea y se guarda en este estado.
 * Es el único estado en el que se puede editar su contenido.
 */
public class DraftState extends AbstractQuestionState {

    /** Completa su contenido → Pendiente de revisión. */
    @Override
    public void submitForReview(Question question) {
        // Solo pasa a revisión si el contenido está completo
        new QuestionValidator().validate(question);
        question.setRejectionReason(null);
        question.setState(new PendingReviewState());
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public String getName() {
        return "Borrador";
    }
}
