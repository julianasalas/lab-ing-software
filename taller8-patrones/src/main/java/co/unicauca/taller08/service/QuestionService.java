package co.unicauca.taller08.service;

import co.unicauca.taller08.domain.Question;
import java.util.List;

/**
 * Subsistema con las operaciones de negocio sobre las preguntas
 * guardadas: guardar, consultar y mover el ciclo de vida.
 * Los cambios de estado los decide el patrón State dentro de Question.
 */
public class QuestionService {

    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {
        this.repository = repository;
    }

    public Question save(Question question) {
        return repository.save(question);
    }

    public Question findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la pregunta con id " + id));
    }

    public List<Question> findAll() {
        return repository.findAll();
    }

    public List<Question> findByState(String stateName) {
        return repository.findByState(stateName);
    }

    public void submitForReview(Question question) {
        question.submitForReview();
    }

    public void assignReviewer(Question question, String reviewer) {
        question.assignReviewer(reviewer);
    }

    public void approve(Question question) {
        question.approve();
    }

    public void reject(Question question, String reason) {
        question.reject(reason);
    }

    public void returnToDraft(Question question) {
        question.returnToDraft();
    }

    public void archive(Question question) {
        question.archive();
    }
}
