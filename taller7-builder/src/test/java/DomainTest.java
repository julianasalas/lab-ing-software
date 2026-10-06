import static org.junit.jupiter.api.Assertions.*;

import domain.DifficultyLevel;
import domain.Option;
import domain.Question;
import domain.QuestionType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas de las clases del dominio (producto y sus partes).
 */
@DisplayName("Dominio")
class DomainTest {

    @Nested
    @DisplayName("Option")
    class OptionTests {

        @Test
        @DisplayName("Crea una opción con etiqueta, texto y si es correcta")
        void createsOption() {
            Option o = new Option("A", "Bogotá", true);
            assertEquals("A", o.getLabel());
            assertEquals("Bogotá", o.getText());
            assertTrue(o.isCorrect());
        }

        @Test
        @DisplayName("Elimina espacios sobrantes")
        void trims() {
            Option o = new Option(" B ", "  Cali ", false);
            assertEquals("B", o.getLabel());
            assertEquals("Cali", o.getText());
        }

        @ParameterizedTest(name = "etiqueta inválida [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Rechaza etiqueta vacía")
        void rejectsBlankLabel(String label) {
            assertThrows(IllegalArgumentException.class, () -> new Option(label, "Texto", false));
        }

        @ParameterizedTest(name = "texto inválido [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Rechaza texto vacío")
        void rejectsBlankText(String text) {
            assertThrows(IllegalArgumentException.class, () -> new Option("A", text, false));
        }

        @Test
        @DisplayName("equals y hashCode comparan etiqueta, texto y si es correcta")
        void equalsAndHashCode() {
            Option a = new Option("A", "Uno", true);
            Option b = new Option("A", "Uno", true);
            Option c = new Option("A", "Uno", false);
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
            assertNotEquals(a, c);
            assertNotEquals(a, null);
            assertNotEquals(a, "A) Uno");
        }

        @Test
        @DisplayName("toString marca la opción correcta")
        void toStringMarksCorrect() {
            assertTrue(new Option("A", "Uno", true).toString().contains("correcta"));
            assertFalse(new Option("B", "Dos", false).toString().contains("correcta"));
        }
    }

    @Nested
    @DisplayName("Question")
    class QuestionTests {

        private Question sample() {
            List<Option> options = List.of(new Option("A", "Uno", false), new Option("B", "Dos", true));
            return new Question(QuestionType.MULTIPLE_CHOICE, "Enunciado de ejemplo", DifficultyLevel.MEDIUM,
                    "Lectura crítica", "Juliana Salas", "Justificación de ejemplo", null, options, null);
        }

        @Test
        @DisplayName("getCorrectOption devuelve la opción marcada")
        void correctOption() {
            assertEquals("B", sample().getCorrectOption().orElseThrow().getLabel());
        }

        @Test
        @DisplayName("Opciones nulas se convierten en lista vacía")
        void nullOptionsBecomeEmpty() {
            Question q = new Question(QuestionType.OPEN, "Enunciado de ejemplo", DifficultyLevel.LOW,
                    "Cat", "Autor", "Justificación", null, null, "Referencia");
            assertNotNull(q.getOptions());
            assertTrue(q.getOptions().isEmpty());
            assertFalse(q.hasOptions());
        }

        @Test
        @DisplayName("La pregunta copia la lista de opciones (inmutable)")
        void copiesOptions() {
            List<Option> options = new ArrayList<>(List.of(new Option("A", "Uno", true)));
            Question q = new Question(QuestionType.MULTIPLE_CHOICE, "Enunciado de ejemplo",
                    DifficultyLevel.LOW, "Cat", "Autor", "Justificación", null, options, null);
            options.clear();
            assertEquals(1, q.getOptions().size());
            assertThrows(UnsupportedOperationException.class, () -> q.getOptions().clear());
        }

        @Test
        @DisplayName("toString muestra los datos principales")
        void toStringShowsData() {
            String text = sample().toString();
            assertTrue(text.contains("Enunciado de ejemplo"));
            assertTrue(text.contains("Media"));
            assertTrue(text.contains("Lectura crítica"));
            assertTrue(text.contains("Juliana Salas"));
            assertTrue(text.contains("(sin fuente)"));
            assertTrue(text.contains("B) Dos"));
        }
    }

    @Nested
    @DisplayName("Enumeraciones")
    class Enums {

        @ParameterizedTest(name = "{0}")
        @EnumSource(QuestionType.class)
        @DisplayName("Todo QuestionType tiene nombre para mostrar")
        void questionTypeDisplayName(QuestionType type) {
            assertFalse(type.getDisplayName().isBlank());
        }

        @ParameterizedTest(name = "{0}")
        @EnumSource(DifficultyLevel.class)
        @DisplayName("Todo DifficultyLevel tiene nombre para mostrar")
        void difficultyDisplayName(DifficultyLevel level) {
            assertFalse(level.getDisplayName().isBlank());
        }

        @Test
        @DisplayName("Existen los tres tipos de pregunta del taller")
        void threeQuestionTypes() {
            assertEquals(3, QuestionType.values().length);
        }
    }
}
