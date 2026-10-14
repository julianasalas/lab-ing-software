package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * Aprobada: la pregunta fue aprobada por el revisor y puede publicarse.
 */
public class ApprovedState extends AbstractQuestionState {

    /** Aprobada → Publicada. */
    @Override
    public void publish(Question question) {
        question.setState(new PublishedState());
    }

    @Override
    public String getName() {
        return "Aprobada";
    }
}
