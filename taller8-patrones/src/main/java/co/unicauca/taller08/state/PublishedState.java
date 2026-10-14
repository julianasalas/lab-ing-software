package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * Publicada: la pregunta queda disponible para usarse en pruebas.
 */
public class PublishedState extends AbstractQuestionState {

    /** Publicada → Archivada. */
    @Override
    public void archive(Question question) {
        question.setState(new ArchivedState());
    }

    @Override
    public boolean isAvailableForTests() {
        return true;
    }

    @Override
    public String getName() {
        return "Publicada";
    }
}
