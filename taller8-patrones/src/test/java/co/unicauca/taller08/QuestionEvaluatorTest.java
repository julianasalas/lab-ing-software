package co.unicauca.taller08;

import static org.junit.jupiter.api.Assertions.*;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.templatemethod.EvaluatorFactory;
import co.unicauca.taller08.templatemethod.MultipleChoiceEvaluator;
import co.unicauca.taller08.templatemethod.QuestionEvaluator;
import co.unicauca.taller08.templatemethod.SingleChoiceEvaluator;
import co.unicauca.taller08.templatemethod.TrueFalseEvaluator;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas del patrón Template Method: evaluación de respuestas.
 */
@DisplayName("Template Method - evaluación de respuestas")
class QuestionEvaluatorTest {

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Algoritmo general (clase abstracta)")
    class AlgoritmoGeneral {

        @Test
        @DisplayName("evaluate() es final: las subclases no pueden cambiar el algoritmo")
        void evaluateIsFinal() throws Exception {
            Method m = QuestionEvaluator.class.getMethod("evaluate", Question.class, List.class);
            assertTrue(Modifier.isFinal(m.getModifiers()));
        }

        @Test
        @DisplayName("validate y compareAnswers son los pasos abstractos")
        void abstractSteps() throws Exception {
            Method validate = QuestionEvaluator.class.getDeclaredMethod("validate", Question.class, List.class);
            Method compare = QuestionEvaluator.class.getDeclaredMethod("compareAnswers", Question.class, List.class);
            assertTrue(Modifier.isAbstract(validate.getModifiers()));
            assertTrue(Modifier.isAbstract(compare.getModifiers()));
        }

        @Test
        @DisplayName("Una pregunta que no está Publicada no se puede evaluar")
        void unpublishedCannotBeEvaluated() {
            Question q = TestQuestions.singleChoice();
            IllegalStateException e = assertThrows(IllegalStateException.class,
                    () -> new SingleChoiceEvaluator().evaluate(q, List.of("B")));
            assertTrue(e.getMessage().contains("Publicada"));
        }

        @Test
        @DisplayName("Una pregunta Archivada tampoco se puede evaluar")
        void archivedCannotBeEvaluated() {
            Question q = TestQuestions.publish(TestQuestions.singleChoice());
            q.archive();
            assertThrows(IllegalStateException.class, () -> new SingleChoiceEvaluator().evaluate(q, List.of("B")));
        }

        @Test
        @DisplayName("Sin respuestas lanza error (paso 1: validar)")
        void emptyAnswers() {
            Question q = TestQuestions.publish(TestQuestions.singleChoice());
            assertThrows(IllegalArgumentException.class, () -> new SingleChoiceEvaluator().evaluate(q, List.of()));
            assertThrows(IllegalArgumentException.class, () -> new SingleChoiceEvaluator().evaluate(q, null));
        }

        @Test
        @DisplayName("Pregunta null lanza error")
        void nullQuestion() {
            assertThrows(IllegalArgumentException.class, () -> new SingleChoiceEvaluator().evaluate(null, List.of("A")));
        }

        @Test
        @DisplayName("Un evaluador no acepta preguntas de otro tipo")
        void wrongType() {
            Question q = TestQuestions.publish(TestQuestions.trueFalse(true));
            assertThrows(IllegalArgumentException.class, () -> new SingleChoiceEvaluator().evaluate(q, List.of("A")));
            assertThrows(IllegalArgumentException.class, () -> new MultipleChoiceEvaluator().evaluate(q, List.of("A")));
        }

        @ParameterizedTest(name = "respuesta [{0}]")
        @ValueSource(strings = {"Z", "8", "cuatro", ""})
        @DisplayName("Una respuesta que no corresponde a ninguna opción se rechaza (paso 2)")
        void unknownAnswer(String answer) {
            Question q = TestQuestions.publish(TestQuestions.singleChoice());
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> new SingleChoiceEvaluator().evaluate(q, List.of(answer)));
            assertTrue(e.getMessage().contains("no corresponde"));
        }

        @Test
        @DisplayName("La retroalimentación por defecto está vacía antes de evaluar")
        void emptyFeedbackBeforeEvaluating() {
            assertEquals("", new SingleChoiceEvaluator().getLastFeedback());
        }

        @ParameterizedTest(name = "{0}")
        @EnumSource(QuestionType.class)
        @DisplayName("La fábrica entrega el evaluador de cada tipo")
        void factory(QuestionType type) {
            QuestionEvaluator evaluator = new EvaluatorFactory().forType(type);
            assertEquals(type, evaluator.getSupportedType());
        }

        @Test
        @DisplayName("La fábrica rechaza un tipo null")
        void factoryNull() {
            assertThrows(IllegalArgumentException.class, () -> new EvaluatorFactory().forType(null));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Selección única")
    class SeleccionUnica {

        private Question q;
        private SingleChoiceEvaluator evaluator;

        @BeforeEach
        void setUp() {
            q = TestQuestions.publish(TestQuestions.singleChoice());
            evaluator = new SingleChoiceEvaluator();
        }

        @ParameterizedTest(name = "[{0}] es correcta")
        @ValueSource(strings = {"B", "b", " B ", "4"})
        @DisplayName("Acepta la letra o el texto de la opción correcta")
        void correctAnswers(String answer) {
            assertTrue(evaluator.evaluate(q, List.of(answer)));
            assertEquals("¡Respuesta correcta!", evaluator.getLastFeedback());
        }

        @ParameterizedTest(name = "[{0}] es incorrecta")
        @ValueSource(strings = {"A", "C", "3", "5"})
        @DisplayName("Una opción incorrecta da resultado falso")
        void wrongAnswers(String answer) {
            assertFalse(evaluator.evaluate(q, List.of(answer)));
            assertTrue(evaluator.getLastFeedback().contains("B) 4"));
        }

        @Test
        @DisplayName("Debe seleccionar exactamente una respuesta")
        void exactlyOne() {
            assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(q, List.of("A", "B")));
        }

        @Test
        @DisplayName("La misma opción repetida cuenta como una sola")
        void duplicatedSameOption() {
            assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(q, List.of("B", "b")));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Selección múltiple")
    class SeleccionMultiple {

        private Question q;
        private MultipleChoiceEvaluator evaluator;

        @BeforeEach
        void setUp() {
            q = TestQuestions.publish(TestQuestions.multipleChoice());
            evaluator = new MultipleChoiceEvaluator();
        }

        @Test
        @DisplayName("Todas las correctas y ninguna incorrecta: correcta")
        void allCorrect() {
            assertTrue(evaluator.evaluate(q, List.of("A", "C")));
        }

        @Test
        @DisplayName("El orden no importa")
        void orderDoesNotMatter() {
            assertTrue(evaluator.evaluate(q, List.of("C", "A")));
        }

        @Test
        @DisplayName("Se puede responder con los textos de las opciones")
        void byText() {
            assertTrue(evaluator.evaluate(q, List.of("2", "7")));
        }

        @Test
        @DisplayName("Respuestas repetidas no afectan el resultado")
        void duplicates() {
            assertTrue(evaluator.evaluate(q, Arrays.asList("A", "a", "C")));
        }

        @Test
        @DisplayName("Solo una de las correctas: incorrecta, e indica cuántas acertó")
        void partial() {
            assertFalse(evaluator.evaluate(q, List.of("A")));
            assertTrue(evaluator.getLastFeedback().contains("Acertó 1 de 2"));
        }

        @Test
        @DisplayName("Las correctas más una incorrecta: incorrecta")
        void extraWrong() {
            assertFalse(evaluator.evaluate(q, List.of("A", "B", "C")));
            assertTrue(evaluator.getLastFeedback().contains("Acertó 2 de 2"));
        }

        @Test
        @DisplayName("Solo incorrectas: incorrecta")
        void onlyWrong() {
            assertFalse(evaluator.evaluate(q, List.of("B", "D")));
            assertTrue(evaluator.getLastFeedback().contains("Acertó 0 de 2"));
        }

        @Test
        @DisplayName("No puede seleccionar más respuestas que opciones")
        void tooMany() {
            List<String> answers = new ArrayList<>(List.of("A", "B", "C", "D", "A"));
            assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(q, answers));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Verdadero/Falso")
    class VerdaderoFalso {

        private TrueFalseEvaluator evaluator;

        @BeforeEach
        void setUp() {
            evaluator = new TrueFalseEvaluator();
        }

        @ParameterizedTest(name = "[{0}] = Verdadero")
        @ValueSource(strings = {"Verdadero", "verdadero", "V", "v", "true", "TRUE", "A"})
        @DisplayName("Variantes de Verdadero")
        void trueVariants(String answer) {
            Question q = TestQuestions.publish(TestQuestions.trueFalse(true));
            assertTrue(evaluator.evaluate(q, List.of(answer)));
        }

        @ParameterizedTest(name = "[{0}] = Falso")
        @ValueSource(strings = {"Falso", "falso", "F", "f", "false", "B"})
        @DisplayName("Variantes de Falso")
        void falseVariants(String answer) {
            Question q = TestQuestions.publish(TestQuestions.trueFalse(false));
            assertTrue(evaluator.evaluate(q, List.of(answer)));
        }

        @Test
        @DisplayName("Responder Falso cuando es Verdadero: incorrecta")
        void wrong() {
            Question q = TestQuestions.publish(TestQuestions.trueFalse(true));
            assertFalse(evaluator.evaluate(q, List.of("F")));
            assertTrue(evaluator.getLastFeedback().contains("Verdadero"));
        }

        @ParameterizedTest(name = "[{0}]")
        @ValueSource(strings = {"Sí", "No", "C", "quizás"})
        @DisplayName("Respuestas que no son V/F se rechazan")
        void invalid(String answer) {
            Question q = TestQuestions.publish(TestQuestions.trueFalse(true));
            assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(q, List.of(answer)));
        }

        @Test
        @DisplayName("Solo se puede seleccionar una respuesta")
        void onlyOne() {
            Question q = TestQuestions.publish(TestQuestions.trueFalse(true));
            assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate(q, List.of("V", "F")));
        }
    }
}
