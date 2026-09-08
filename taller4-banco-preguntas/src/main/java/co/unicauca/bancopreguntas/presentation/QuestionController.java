package co.unicauca.bancopreguntas.presentation;

import co.unicauca.bancopreguntas.domain.Question;
import co.unicauca.bancopreguntas.domain.QuestionService;
import co.unicauca.bancopreguntas.domain.QuestionState;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador del micro patron MVC.
 *
 * Capa: presentacion.
 *
 * Es el unico punto por el que las vistas hablan con el modelo: ninguna ventana
 * invoca directamente a QuestionService. Asi la vista no contiene reglas de
 * negocio y el modelo no sabe que existe Swing.
 */
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        if (service == null) {
            throw new IllegalArgumentException("El servicio es obligatorio");
        }
        this.service = service;
    }

    /** Preguntas para llenar el comboBox de la ventana principal. */
    public List<Question> listQuestions() {
        return service.listQuestions();
    }

    /** Carga los datos de una pregunta en el formulario. */
    public Optional<Question> loadQuestion(String id) {
        return service.findById(id);
    }

    /**
     * Solicita el cambio de estado. El servicio se encarga de notificar a las
     * vistas observadoras, por eso aqui no hay ninguna llamada de refresco.
     */
    public Question changeState(String questionId, QuestionState newState) {
        return service.changeState(questionId, newState);
    }

    /** Conteo de preguntas por estado (vista de estadisticas). */
    public Map<QuestionState, Integer> getStatistics() {
        return service.getStatistics();
    }

    /** Porcentaje de preguntas por estado (vista grafica de pastel). */
    public Map<QuestionState, Double> getPercentages() {
        return service.getPercentages();
    }

    /** Total de preguntas del banco. */
    public int getTotalQuestions() {
        return service.getTotalQuestions();
    }
}
