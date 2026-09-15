package co.unicauca.bancopreguntas.domain;

import co.unicauca.bancopreguntas.infra.Subject;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de dominio del banco de preguntas.
 *
 * Capa: dominio. Rol en MVC: <b>Modelo</b>. Rol en Observer: <b>Sujeto</b>.
 *
 * Concentra las reglas de negocio (cambio de estado y calculo de estadisticas)
 * y avisa a sus observadores cada vez que el estado del banco cambia. No conoce
 * Swing ni ninguna clase de la capa de presentacion.
 */
public class QuestionService extends Subject {

    private final QuestionRepository repository;

    /**
     * Recibe el repositorio por constructor (inyeccion de dependencias): el
     * servicio depende de la abstraccion QuestionRepository, no de una
     * implementacion concreta.
     */
    public QuestionService(QuestionRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("El repositorio es obligatorio");
        }
        this.repository = repository;
    }

    /** Todas las preguntas, para alimentar el comboBox de la vista principal. */
    public List<Question> listQuestions() {
        return repository.findAll();
    }

    /** Busca una pregunta por su id. */
    public Optional<Question> findById(String id) {
        return repository.findById(id);
    }

    /**
     * Regla de negocio principal: cambia el estado de una pregunta y notifica
     * a todas las vistas registradas.
     *
     * @return la pregunta ya actualizada.
     * @throws IllegalArgumentException si la pregunta no existe o el estado es nulo.
     */
    public Question changeState(String questionId, QuestionState newState) {
        if (newState == null) {
            throw new IllegalArgumentException("El nuevo estado es obligatorio");
        }
        Question question = repository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe la pregunta con id " + questionId));

        question.setState(newState);
        Question saved = repository.save(question);

        // Aqui esta el corazon del patron Observer: un unico punto de
        // notificacion que dispara el refresco de TODAS las vistas suscritas.
        notifyObservers();
        return saved;
    }

    /** Cuantas preguntas hay en cada estado. Siempre incluye los tres estados. */
    public Map<QuestionState, Integer> getStatistics() {
        Map<QuestionState, Integer> statistics = new EnumMap<>(QuestionState.class);
        for (QuestionState state : QuestionState.values()) {
            statistics.put(state, 0);
        }
        for (Question question : repository.findAll()) {
            statistics.merge(question.getState(), 1, Integer::sum);
        }
        return statistics;
    }

    /** Porcentaje de preguntas por estado. Con el banco vacio retorna 0.0 en todos. */
    public Map<QuestionState, Double> getPercentages() {
        Map<QuestionState, Integer> statistics = getStatistics();
        int total = getTotalQuestions();

        Map<QuestionState, Double> percentages = new EnumMap<>(QuestionState.class);
        for (QuestionState state : QuestionState.values()) {
            double percentage = (total == 0)
                    ? 0.0
                    : (statistics.get(state) * 100.0) / total;
            percentages.put(state, percentage);
        }
        return percentages;
    }

    /** Total de preguntas del banco. */
    public int getTotalQuestions() {
        return repository.findAll().size();
    }
}
