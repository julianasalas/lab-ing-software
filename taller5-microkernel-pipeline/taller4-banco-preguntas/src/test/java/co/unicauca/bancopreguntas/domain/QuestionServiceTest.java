package co.unicauca.bancopreguntas.domain;

import co.unicauca.bancopreguntas.infra.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la clase de dominio QuestionService.
 *
 * Se usa un repositorio falso (test double) en memoria, sin tocar la
 * implementacion real: es posible justamente porque el servicio depende de la
 * interfaz QuestionRepository y no de una clase concreta (principio DIP).
 */
class QuestionServiceTest {

    /** Repositorio falso, controlado por la prueba. */
    private static class FakeQuestionRepository implements QuestionRepository {

        private final Map<String, Question> data = new LinkedHashMap<>();

        @Override
        public List<Question> findAll() {
            return new ArrayList<>(data.values());
        }

        @Override
        public Optional<Question> findById(String id) {
            return Optional.ofNullable(data.get(id));
        }

        @Override
        public Question save(Question question) {
            data.put(question.getId(), question);
            return question;
        }
    }

    /** Observador falso que solo cuenta cuantas veces fue notificado. */
    private static class CountingObserver implements Observer {

        private int updates = 0;

        @Override
        public void update() {
            updates++;
        }

        int getUpdates() {
            return updates;
        }
    }

    private FakeQuestionRepository repository;
    private QuestionService service;

    private static Question newQuestion(String id, QuestionState state) {
        return new Question(
                id,
                "Pregunta " + id,
                "Enunciado de la pregunta " + id,
                new QuestionDistractors("Opcion A", "Opcion B", "Opcion C", "Opcion D"),
                0,
                state);
    }

    @BeforeEach
    void setUp() {
        repository = new FakeQuestionRepository();
        service = new QuestionService(repository);
    }

    @Test
    @DisplayName("changeState cambia el estado de la pregunta y lo persiste")
    void changeStateActualizaElEstado() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));

        Question updated = service.changeState("P-001", QuestionState.PENDIENTE_REVISION);

        assertEquals(QuestionState.PENDIENTE_REVISION, updated.getState());
        assertEquals(QuestionState.PENDIENTE_REVISION,
                repository.findById("P-001").orElseThrow().getState());
    }

    @Test
    @DisplayName("changeState notifica a todas las vistas observadoras")
    void changeStateNotificaObservadores() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));

        CountingObserver view1 = new CountingObserver();
        CountingObserver view2 = new CountingObserver();
        service.attach(view1);
        service.attach(view2);

        service.changeState("P-001", QuestionState.ELIMINADA);

        assertEquals(1, view1.getUpdates(), "La vista 1 debio ser notificada una vez");
        assertEquals(1, view2.getUpdates(), "La vista 2 debio ser notificada una vez");
    }

    @Test
    @DisplayName("un observador dado de baja deja de recibir notificaciones")
    void observadorDesuscritoNoRecibeNotificaciones() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));

        CountingObserver view = new CountingObserver();
        service.attach(view);
        service.detach(view);

        service.changeState("P-001", QuestionState.ELIMINADA);

        assertEquals(0, view.getUpdates());
    }

    @Test
    @DisplayName("attach ignora observadores duplicados")
    void attachIgnoraDuplicados() {
        CountingObserver view = new CountingObserver();
        service.attach(view);
        service.attach(view);

        assertEquals(1, service.countObservers());
    }

    @Test
    @DisplayName("changeState lanza excepcion si la pregunta no existe")
    void changeStateFallaConIdInexistente() {
        assertThrows(IllegalArgumentException.class,
                () -> service.changeState("NO-EXISTE", QuestionState.BORRADOR));
    }

    @Test
    @DisplayName("changeState lanza excepcion si el estado es nulo")
    void changeStateFallaConEstadoNulo() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeState("P-001", null));
    }

    @Test
    @DisplayName("getStatistics cuenta las preguntas de cada estado")
    void getStatisticsCuentaPorEstado() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));
        repository.save(newQuestion("P-002", QuestionState.BORRADOR));
        repository.save(newQuestion("P-003", QuestionState.PENDIENTE_REVISION));
        repository.save(newQuestion("P-004", QuestionState.ELIMINADA));

        Map<QuestionState, Integer> statistics = service.getStatistics();

        assertEquals(2, statistics.get(QuestionState.BORRADOR));
        assertEquals(1, statistics.get(QuestionState.PENDIENTE_REVISION));
        assertEquals(1, statistics.get(QuestionState.ELIMINADA));
    }

    @Test
    @DisplayName("getStatistics incluye en cero los estados sin preguntas")
    void getStatisticsIncluyeEstadosVacios() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));

        Map<QuestionState, Integer> statistics = service.getStatistics();

        assertEquals(QuestionState.values().length, statistics.size());
        assertEquals(0, statistics.get(QuestionState.ELIMINADA));
    }

    @Test
    @DisplayName("getPercentages calcula el porcentaje por estado y suma 100")
    void getPercentagesCalculaPorcentajes() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));
        repository.save(newQuestion("P-002", QuestionState.BORRADOR));
        repository.save(newQuestion("P-003", QuestionState.PENDIENTE_REVISION));
        repository.save(newQuestion("P-004", QuestionState.ELIMINADA));

        Map<QuestionState, Double> percentages = service.getPercentages();

        assertEquals(50.0, percentages.get(QuestionState.BORRADOR), 0.001);
        assertEquals(25.0, percentages.get(QuestionState.PENDIENTE_REVISION), 0.001);
        assertEquals(25.0, percentages.get(QuestionState.ELIMINADA), 0.001);

        double total = percentages.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(100.0, total, 0.001);
    }

    @Test
    @DisplayName("getPercentages retorna cero cuando el banco esta vacio")
    void getPercentagesConBancoVacio() {
        Map<QuestionState, Double> percentages = service.getPercentages();

        for (QuestionState state : QuestionState.values()) {
            assertEquals(0.0, percentages.get(state), 0.001);
        }
        assertEquals(0, service.getTotalQuestions());
    }

    @Test
    @DisplayName("las estadisticas cambian despues de un cambio de estado")
    void estadisticasSeActualizanTrasCambioDeEstado() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));
        repository.save(newQuestion("P-002", QuestionState.BORRADOR));

        assertEquals(2, service.getStatistics().get(QuestionState.BORRADOR));

        service.changeState("P-001", QuestionState.ELIMINADA);

        assertEquals(1, service.getStatistics().get(QuestionState.BORRADOR));
        assertEquals(1, service.getStatistics().get(QuestionState.ELIMINADA));
    }

    @Test
    @DisplayName("el constructor rechaza un repositorio nulo")
    void constructorRechazaRepositorioNulo() {
        assertThrows(IllegalArgumentException.class, () -> new QuestionService(null));
    }

    @Test
    @DisplayName("listQuestions devuelve todas las preguntas del repositorio")
    void listQuestionsDevuelveTodas() {
        repository.save(newQuestion("P-001", QuestionState.BORRADOR));
        repository.save(newQuestion("P-002", QuestionState.ELIMINADA));

        assertEquals(2, service.listQuestions().size());
        assertTrue(service.findById("P-001").isPresent());
    }
}
