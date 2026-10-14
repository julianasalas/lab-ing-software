package co.unicauca.taller08.service;

import co.unicauca.taller08.domain.Question;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Subsistema de almacenamiento. Guarda las preguntas en memoria y les
 * asigna un id consecutivo.
 */
public class QuestionRepository {

    private final Map<Long, Question> questions = new LinkedHashMap<>();
    private long nextId = 1;

    public Question save(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("La pregunta no puede ser null");
        }
        if (question.getId() == null) {
            question.setId(nextId++);
        }
        questions.put(question.getId(), question);
        return question;
    }

    public Optional<Question> findById(Long id) {
        return Optional.ofNullable(questions.get(id));
    }

    public List<Question> findAll() {
        return new ArrayList<>(questions.values());
    }

    public List<Question> findByState(String stateName) {
        List<Question> result = new ArrayList<>();
        for (Question q : questions.values()) {
            if (q.getStateName().equalsIgnoreCase(stateName)) {
                result.add(q);
            }
        }
        return result;
    }

    public int count() {
        return questions.size();
    }
}
