package co.unicauca.taller08;

import static org.junit.jupiter.api.Assertions.*;

import co.unicauca.taller08.adapter.ExternalQuestionAdapter;
import co.unicauca.taller08.adapter.ExternalQuestionService;
import co.unicauca.taller08.decorator.HighDifficultyQuestion;
import co.unicauca.taller08.decorator.QuestionComponent;
import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.facade.QuestionBankFacade;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del patrón Facade: el cliente hace todo a través de QuestionBankFacade.
 */
@DisplayName("Facade - QuestionBankFacade")
class QuestionBankFacadeTest {

    private QuestionBankFacade facade;

    @BeforeEach
    void setUp() {
        facade = new QuestionBankFacade();
    }

    private Long publishedSingleChoice() {
        Long id = facade.createQuestion(TestQuestions.singleChoice()).getId();
        facade.submitForReview(id);
        facade.assignReviewer(id, "Revisor");
        facade.approveQuestion(id);
        facade.publishQuestion(id);
        return id;
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Crear y consultar (requerimientos 1 a 5)")
    class CrearYConsultar {

        @Test
        @DisplayName("createQuestion valida, guarda y asigna id")
        void createQuestion() {
            Question q = facade.createQuestion(TestQuestions.singleChoice());
            assertNotNull(q.getId());
            assertSame(q, facade.findQuestion(q.getId()));
            assertEquals("Borrador", q.getStateName());
        }

        @Test
        @DisplayName("createQuestion rechaza una pregunta incompleta y no la guarda")
        void createInvalid() {
            Question q = new Question("Pregunta sin opciones válida", QuestionType.SINGLE_CHOICE);
            assertThrows(IllegalArgumentException.class, () -> facade.createQuestion(q));
            assertTrue(facade.listQuestions().isEmpty());
        }

        @Test
        @DisplayName("Se pueden crear los tres tipos de pregunta")
        void threeTypes() {
            facade.createQuestion(TestQuestions.singleChoice());
            facade.createQuestion(TestQuestions.multipleChoice());
            facade.createQuestion(TestQuestions.trueFalse(true));
            List<Question> all = facade.listQuestions();
            assertEquals(3, all.size());
            assertEquals(QuestionType.SINGLE_CHOICE, all.get(0).getType());
            assertEquals(QuestionType.MULTIPLE_CHOICE, all.get(1).getType());
            assertEquals(QuestionType.TRUE_FALSE, all.get(2).getType());
        }

        @Test
        @DisplayName("Crear borrador por tipo y agregarle opciones después")
        void draftThenOptions() {
            Question q = facade.createDraftQuestion("¿Cuál es el planeta más grande?", QuestionType.SINGLE_CHOICE);
            facade.addOption(q.getId(), "Marte", false);
            facade.addOption(q.getId(), "Júpiter", true);
            facade.addOption(q.getId(), "Venus", false);
            assertTrue(facade.getValidationErrors(q.getId()).isEmpty());
            assertEquals(List.of("B"), q.getCorrectLabels());
        }

        @Test
        @DisplayName("Crear borrador exige seleccionar el tipo")
        void draftNeedsType() {
            assertThrows(IllegalArgumentException.class, () -> facade.createDraftQuestion("Enunciado de prueba", null));
        }

        @Test
        @DisplayName("Los errores de validación se pueden consultar")
        void validationErrors() {
            Question q = facade.createDraftQuestion("¿Pregunta incompleta?", QuestionType.MULTIPLE_CHOICE);
            assertFalse(facade.getValidationErrors(q.getId()).isEmpty());
        }

        @Test
        @DisplayName("Consultar un id inexistente lanza error")
        void unknownId() {
            assertThrows(IllegalArgumentException.class, () -> facade.findQuestion(99L));
        }

        @Test
        @DisplayName("Consultar por estado")
        void byState() {
            publishedSingleChoice();
            facade.createQuestion(TestQuestions.trueFalse(false));
            assertEquals(1, facade.listQuestionsByState("Publicada").size());
            assertEquals(1, facade.listQuestionsByState("borrador").size());
            assertEquals(0, facade.listQuestionsByState("Archivada").size());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Ciclo de vida (requerimiento 6)")
    class CicloDeVida {

        @Test
        @DisplayName("Ciclo completo a través de la fachada")
        void fullCycle() {
            Long id = publishedSingleChoice();
            assertEquals("Publicada", facade.findQuestion(id).getStateName());
            facade.archiveQuestion(id);
            assertEquals("Archivada", facade.findQuestion(id).getStateName());
        }

        @Test
        @DisplayName("approveAndPublish (como en la guía) aprueba y publica en un paso")
        void approveAndPublish() {
            Long id = facade.createQuestion(TestQuestions.singleChoice()).getId();
            facade.submitForReview(id);
            facade.assignReviewer(id, "Revisor");
            facade.approveAndPublish(id);
            assertEquals("Publicada", facade.findQuestion(id).getStateName());
        }

        @Test
        @DisplayName("La fachada respeta los permisos del State: Borrador → Publicada no permitido")
        void respectsState() {
            Long id = facade.createQuestion(TestQuestions.singleChoice()).getId();
            assertThrows(IllegalStateException.class, () -> facade.publishQuestion(id));
            assertThrows(IllegalStateException.class, () -> facade.approveAndPublish(id));
            assertEquals("Borrador", facade.findQuestion(id).getStateName());
        }

        @Test
        @DisplayName("Rechazar y devolver a borrador")
        void rejectAndReturn() {
            Long id = facade.createQuestion(TestQuestions.singleChoice()).getId();
            facade.submitForReview(id);
            facade.assignReviewer(id, "Revisor");
            facade.rejectQuestion(id, "Ajustar opciones");
            assertEquals("Rechazada", facade.findQuestion(id).getStateName());
            facade.returnToDraft(id);
            facade.addOption(id, "6", false);
            assertEquals(4, facade.findQuestion(id).getOptions().size());
        }

        @Test
        @DisplayName("No se pueden agregar opciones fuera de Borrador")
        void noOptionsOutsideDraft() {
            Long id = publishedSingleChoice();
            assertThrows(IllegalStateException.class, () -> facade.addOption(id, "Otra", false));
        }

        @Test
        @DisplayName("Solo las publicadas y no archivadas están disponibles para pruebas")
        void availableForTests() {
            Long a = publishedSingleChoice();
            publishedSingleChoice();
            assertEquals(2, facade.listAvailableForTests().size());
            facade.archiveQuestion(a);
            assertEquals(1, facade.listAvailableForTests().size());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Evaluar, decorar e importar (requerimientos 7 a 10)")
    class OtrosPatrones {

        @Test
        @DisplayName("Evaluar una respuesta correcta e incorrecta")
        void evaluate() {
            Long id = publishedSingleChoice();
            assertTrue(facade.evaluateAnswer(id, List.of("B")));
            assertEquals("¡Respuesta correcta!", facade.getLastFeedback());
            assertFalse(facade.evaluateAnswer(id, List.of("A")));
            assertTrue(facade.getLastFeedback().startsWith("Respuesta incorrecta"));
        }

        @Test
        @DisplayName("La fachada escoge el evaluador según el tipo")
        void picksEvaluatorByType() {
            Question mc = TestQuestions.publish(facade.createQuestion(TestQuestions.multipleChoice()));
            Question tf = TestQuestions.publish(facade.createQuestion(TestQuestions.trueFalse(false)));
            assertTrue(facade.evaluateAnswer(mc.getId(), List.of("A", "C")));
            assertTrue(facade.evaluateAnswer(tf.getId(), List.of("F")));
        }

        @Test
        @DisplayName("No se puede evaluar una pregunta que no está publicada")
        void evaluateUnpublished() {
            Long id = facade.createQuestion(TestQuestions.singleChoice()).getId();
            assertThrows(IllegalStateException.class, () -> facade.evaluateAnswer(id, List.of("B")));
        }

        @Test
        @DisplayName("Obtener la pregunta como componente y decorarla")
        void decorate() {
            Long id = publishedSingleChoice();
            QuestionComponent c = new HighDifficultyQuestion(facade.getQuestionComponent(id));
            assertTrue(c.getDescription().endsWith("[ALTA DIFICULTAD]"));
            assertEquals(1.5, c.getScore());
        }

        @Test
        @DisplayName("Importar una pregunta externa la guarda en el banco")
        void importQuestion() {
            Question q = facade.importQuestion(new ExternalQuestionAdapter(new ExternalQuestionService()));
            assertNotNull(q.getId());
            assertSame(q, facade.findQuestion(q.getId()));
            assertEquals(1, facade.listQuestions().size());
        }

        @Test
        @DisplayName("Una pregunta importada se completa y se publica con la fachada")
        void importAndPublish() {
            Long id = facade.importQuestion(new ExternalQuestionAdapter(new ExternalQuestionService())).getId();
            facade.addOption(id, "Medellín", false);
            facade.addOption(id, "Cali", false);
            facade.submitForReview(id);
            facade.assignReviewer(id, "Revisor");
            facade.approveAndPublish(id);
            assertTrue(facade.evaluateAnswer(id, List.of("Bogotá")));
        }

        @Test
        @DisplayName("Importar con proveedor null lanza error")
        void importNull() {
            assertThrows(IllegalArgumentException.class, () -> facade.importQuestion(null));
        }
    }
}
