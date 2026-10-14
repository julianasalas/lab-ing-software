package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * Comportamiento por defecto: toda operación está NO PERMITIDA.
 * Cada estado concreto sobrescribe solo las transiciones que sí permite,
 * así no se repite el mismo código de error en todos los estados.
 */
public abstract class AbstractQuestionState implements QuestionState {

    @Override
    public void submitForReview(Question question) {
        throw notAllowed("enviar a revisión");
    }

    @Override
    public void assignReviewer(Question question, String reviewer) {
        throw notAllowed("asignar revisor");
    }

    @Override
    public void approve(Question question) {
        throw notAllowed("aprobar");
    }

    @Override
    public void reject(Question question, String reason) {
        throw notAllowed("rechazar");
    }

    @Override
    public void returnToDraft(Question question) {
        throw notAllowed("devolver a borrador");
    }

    @Override
    public void publish(Question question) {
        throw notAllowed("publicar");
    }

    @Override
    public void archive(Question question) {
        throw notAllowed("archivar");
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean isAvailableForTests() {
        return false;
    }

    protected IllegalStateException notAllowed(String operation) {
        return new IllegalStateException(
                "Operación no permitida: no se puede " + operation + " una pregunta en estado " + getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
