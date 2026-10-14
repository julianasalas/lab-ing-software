package co.unicauca.taller08.service;

import co.unicauca.taller08.domain.Question;
import java.util.ArrayList;
import java.util.List;

/**
 * Subsistema que publica preguntas aprobadas para que puedan usarse en pruebas.
 */
public class QuestionPublisher {

    private final List<Question> published = new ArrayList<>();

    public void publish(Question question) {
        question.publish(); // el estado decide si se puede publicar
        published.add(question);
    }

    /** Preguntas publicadas que todavía se pueden usar en pruebas (no archivadas). */
    public List<Question> getAvailableForTests() {
        List<Question> result = new ArrayList<>();
        for (Question q : published) {
            if (q.isAvailableForTests()) {
                result.add(q);
            }
        }
        return result;
    }
}
