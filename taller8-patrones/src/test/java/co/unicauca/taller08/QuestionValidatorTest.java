package co.unicauca.taller08;

import static org.junit.jupiter.api.Assertions.*;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.service.QuestionRepository;
import co.unicauca.taller08.service.QuestionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas de la validación de preguntas y de las clases del dominio.
 */
@DisplayName("Validación y dominio")
class QuestionValidatorTest {

    private final QuestionValidator validator = new QuestionValidator();

    private static Question withOptions(QuestionType type, int total, int correct) {
        Question q = new Question("Enunciado de prueba suficientemente largo", type);
        for (int i = 0; i < total; i++) {
            q.addOption("Opción " + (i + 1), i < correct);
        }
        return q;
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Reglas comunes")
    class Comunes {

        @Test
        @DisplayName("Las preguntas de ejemplo son válidas")
        void samplesAreValid() {
            assertTrue(validator.isValid(TestQuestions.singleChoice()));
            assertTrue(validator.isValid(TestQuestions.multipleChoice()));
            assertTrue(validator.isValid(TestQuestions.trueFalse(true)));
        }

        @ParameterizedTest(name = "enunciado [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "Corto"})
        @DisplayName("El enunciado es obligatorio y de al menos 10 caracteres")
        void statement(String statement) {
            Question q = TestQuestions.singleChoice();
            q.setStatement(statement);
            assertFalse(validator.isValid(q));
            assertThrows(IllegalArgumentException.class, () -> validator.validate(q));
        }

        @Test
        @DisplayName("Debe tener tipo")
        void typeRequired() {
            Question q = new Question("Enunciado de prueba sin tipo", null);
            assertTrue(validator.getErrors(q).contains("debe seleccionar el tipo de pregunta"));
        }

        @Test
        @DisplayName("Una pregunta null no es válida")
        void nullQuestion() {
            assertFalse(validator.isValid(null));
        }

        @Test
        @DisplayName("Reporta varios errores a la vez")
        void severalErrors() {
            Question q = new Question("Corto", QuestionType.SINGLE_CHOICE);
            assertEquals(3, validator.getErrors(q).size());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Reglas por tipo")
    class PorTipo {

        @ParameterizedTest(name = "{0} opciones")
        @ValueSource(ints = {3, 4, 5})
        @DisplayName("Única respuesta: 3 a 5 opciones válidas")
        void singleValidCounts(int total) {
            assertTrue(validator.isValid(withOptions(QuestionType.SINGLE_CHOICE, total, 1)));
        }

        @ParameterizedTest(name = "{0} opciones")
        @ValueSource(ints = {0, 1, 2, 6})
        @DisplayName("Única respuesta: menos de 3 o más de 5 opciones no es válido")
        void singleInvalidCounts(int total) {
            assertFalse(validator.isValid(withOptions(QuestionType.SINGLE_CHOICE, total, Math.min(total, 1))));
        }

        @ParameterizedTest(name = "{0} correctas")
        @ValueSource(ints = {0, 2, 3})
        @DisplayName("Única respuesta: debe tener exactamente 1 correcta")
        void singleCorrectCount(int correct) {
            assertFalse(validator.isValid(withOptions(QuestionType.SINGLE_CHOICE, 4, correct)));
        }

        @ParameterizedTest(name = "{0} correctas de 5")
        @ValueSource(ints = {2, 3, 4})
        @DisplayName("Múltiples respuestas: al menos 2 correctas y no todas")
        void multipleValid(int correct) {
            assertTrue(validator.isValid(withOptions(QuestionType.MULTIPLE_CHOICE, 5, correct)));
        }

        @ParameterizedTest(name = "{0} correctas de 4")
        @ValueSource(ints = {0, 1, 4})
        @DisplayName("Múltiples respuestas: 0, 1 o todas correctas no es válido")
        void multipleInvalidCorrect(int correct) {
            assertFalse(validator.isValid(withOptions(QuestionType.MULTIPLE_CHOICE, 4, correct)));
        }

        @ParameterizedTest(name = "{0} opciones")
        @ValueSource(ints = {2, 7})
        @DisplayName("Múltiples respuestas: entre 3 y 6 opciones")
        void multipleInvalidCount(int total) {
            assertFalse(validator.isValid(withOptions(QuestionType.MULTIPLE_CHOICE, total, 2)));
        }

        @Test
        @DisplayName("Verdadero/Falso: debe tener las opciones Verdadero y Falso")
        void trueFalseOptions() {
            Question q = new Question("El sol es una estrella del sistema solar.", QuestionType.TRUE_FALSE);
            q.addOption("Sí", true);
            q.addOption("No", false);
            assertFalse(validator.isValid(q));
        }

        @Test
        @DisplayName("Verdadero/Falso: con una sola opción no es válida")
        void trueFalseOneOption() {
            Question q = new Question("El sol es una estrella del sistema solar.", QuestionType.TRUE_FALSE);
            q.addOption("Verdadero", true);
            assertFalse(validator.isValid(q));
        }

        @Test
        @DisplayName("Verdadero/Falso: las dos marcadas como correctas no es válido")
        void trueFalseBothCorrect() {
            Question q = new Question("El sol es una estrella del sistema solar.", QuestionType.TRUE_FALSE);
            q.addOption("Verdadero", true);
            q.addOption("Falso", true);
            assertFalse(validator.isValid(q));
        }

        @Test
        @DisplayName("addTrueFalseOptions solo sirve para preguntas Verdadero/Falso")
        void addTrueFalseOnlyForTF() {
            assertThrows(IllegalArgumentException.class,
                    () -> QuestionValidator.addTrueFalseOptions(TestQuestions.singleChoice(), true));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Dominio y repositorio")
    class Dominio {

        @Test
        @DisplayName("Las opciones reciben letras A, B, C en orden")
        void labels() {
            Question q = TestQuestions.singleChoice();
            assertEquals("A", q.getOptions().get(0).getLabel());
            assertEquals("C", q.getOptions().get(2).getLabel());
        }

        @Test
        @DisplayName("No se permiten opciones repetidas")
        void duplicatedOption() {
            Question q = TestQuestions.singleChoice();
            assertThrows(IllegalArgumentException.class, () -> q.addOption(" 4 ", false));
        }

        @ParameterizedTest(name = "texto [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("No se permiten opciones vacías")
        void emptyOption(String text) {
            Question q = TestQuestions.singleChoice();
            assertThrows(IllegalArgumentException.class, () -> q.addOption(text, false));
        }

        @Test
        @DisplayName("Buscar opción por letra o texto")
        void findOption() {
            Question q = TestQuestions.singleChoice();
            assertEquals("B", q.findOption("b").orElseThrow().getLabel());
            assertEquals("B", q.findOption("4").orElseThrow().getLabel());
            assertTrue(q.findOption("Z").isEmpty());
            assertTrue(q.findOption(null).isEmpty());
        }

        @Test
        @DisplayName("La lista de opciones no se puede modificar desde afuera")
        void unmodifiableOptions() {
            Question q = TestQuestions.singleChoice();
            assertThrows(UnsupportedOperationException.class, () -> q.getOptions().clear());
        }

        @Test
        @DisplayName("clearOptions borra las opciones en Borrador")
        void clearOptions() {
            Question q = TestQuestions.singleChoice();
            q.clearOptions();
            assertTrue(q.getOptions().isEmpty());
        }

        @Test
        @DisplayName("toString muestra id, estado, tipo y opciones")
        void toStringShowsData() {
            Question q = TestQuestions.singleChoice();
            q.setId(7L);
            String s = q.toString();
            assertTrue(s.contains("#7"));
            assertTrue(s.contains("[Borrador]"));
            assertTrue(s.contains("única respuesta"));
            assertTrue(s.contains("B) 4  (correcta)"));
        }

        @Test
        @DisplayName("El repositorio asigna ids consecutivos")
        void repositoryIds() {
            QuestionRepository repo = new QuestionRepository();
            Question a = repo.save(TestQuestions.singleChoice());
            Question b = repo.save(TestQuestions.trueFalse(true));
            assertEquals(Long.valueOf(1), a.getId());
            assertEquals(Long.valueOf(2), b.getId());
            assertEquals(2, repo.count());
        }

        @Test
        @DisplayName("Guardar de nuevo la misma pregunta no duplica")
        void repositoryUpdate() {
            QuestionRepository repo = new QuestionRepository();
            Question a = repo.save(TestQuestions.singleChoice());
            repo.save(a);
            assertEquals(1, repo.count());
        }

        @Test
        @DisplayName("El repositorio no guarda null")
        void repositoryNull() {
            assertThrows(IllegalArgumentException.class, () -> new QuestionRepository().save(null));
        }
    }
}
