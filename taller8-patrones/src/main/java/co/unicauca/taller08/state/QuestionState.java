package co.unicauca.taller08.state;

import co.unicauca.taller08.domain.Question;

/**
 * Patrón State: interfaz común de los estados de una pregunta.
 *
 * Cada operación del ciclo de vida recibe la pregunta (el contexto) para
 * que el estado concreto pueda cambiarla al siguiente estado.
 */
public interface QuestionState {

    void submitForReview(Question question);

    void assignReviewer(Question question, String reviewer);

    void approve(Question question);

    void reject(Question question, String reason);

    void returnToDraft(Question question);

    void publish(Question question);

    void archive(Question question);

    String getName();

    /** true si en este estado se puede modificar el contenido de la pregunta. */
    boolean isEditable();

    /** true si en este estado la pregunta se puede usar en una prueba. */
    boolean isAvailableForTests();
}
