import static org.junit.jupiter.api.Assertions.*;

import builder.TrueFalseQuestionBuilder;
import domain.DifficultyLevel;
import domain.Option;
import domain.Question;
import domain.QuestionType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias del builder de preguntas Verdadero / Falso.
 */
@DisplayName("TrueFalseQuestionBuilder")
class TrueFalseQuestionBuilderTest {

    private static final String STATEMENT = "La tesis es la idea principal que defiende un texto argumentativo.";
    private static final String CATEGORY = "Lectura crítica";
    private static final String AUTHOR = "Juliana Salas";
    private static final String JUSTIFICATION = "La tesis es la postura central que el autor sostiene.";

    private TrueFalseQuestionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new TrueFalseQuestionBuilder();
    }

    private void fillCommonFields() {
        builder.buildStatement(STATEMENT);
        builder.buildDifficulty(DifficultyLevel.LOW);
        builder.buildCategory(CATEGORY);
        builder.buildAuthor(AUTHOR);
        builder.buildJustification(JUSTIFICATION);
    }

    private void fillValidQuestion() {
        fillCommonFields();
        builder.buildCorrectAnswer("Verdadero");
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Construcción correcta")
    class ConstruccionCorrecta {

        @Test
        @DisplayName("Construye una pregunta Verdadero/Falso válida")
        void buildsValidQuestion() {
            fillValidQuestion();
            Question q = builder.build();
            assertEquals(QuestionType.TRUE_FALSE, q.getType());
            assertEquals(STATEMENT, q.getStatement());
            assertEquals(CATEGORY, q.getCategory());
            assertEquals(AUTHOR, q.getAuthor());
            assertEquals(JUSTIFICATION, q.getJustification());
        }

        @Test
        @DisplayName("Genera automáticamente las opciones A) Verdadero y B) Falso")
        void generatesTrueFalseOptions() {
            fillValidQuestion();
            List<Option> opts = builder.build().getOptions();
            assertEquals(2, opts.size());
            assertEquals("A", opts.get(0).getLabel());
            assertEquals("Verdadero", opts.get(0).getText());
            assertEquals("B", opts.get(1).getLabel());
            assertEquals("Falso", opts.get(1).getText());
        }

        @Test
        @DisplayName("Si la respuesta es Verdadero, solo la opción A es correcta")
        void trueAnswerMarksA() {
            fillCommonFields();
            builder.buildCorrectAnswer("Verdadero");
            Question q = builder.build();
            assertTrue(q.getOptions().get(0).isCorrect());
            assertFalse(q.getOptions().get(1).isCorrect());
        }

        @Test
        @DisplayName("Si la respuesta es Falso, solo la opción B es correcta")
        void falseAnswerMarksB() {
            fillCommonFields();
            builder.buildCorrectAnswer("Falso");
            Question q = builder.build();
            assertFalse(q.getOptions().get(0).isCorrect());
            assertTrue(q.getOptions().get(1).isCorrect());
        }

        @Test
        @DisplayName("Siempre hay exactamente una opción correcta")
        void exactlyOneCorrect() {
            fillValidQuestion();
            long correct = builder.build().getOptions().stream().filter(Option::isCorrect).count();
            assertEquals(1, correct);
        }

        @Test
        @DisplayName("No tiene respuesta de referencia y la fuente es opcional")
        void noReferenceAnswerAndOptionalSource() {
            fillValidQuestion();
            Question q = builder.build();
            assertNull(q.getReferenceAnswer());
            assertNull(q.getSource());
        }

        @Test
        @DisplayName("Conserva la fuente cuando se proporciona")
        void keepsSource() {
            fillValidQuestion();
            builder.buildSource("Cassany, D. Tras las líneas");
            assertEquals("Cassany, D. Tras las líneas", builder.build().getSource());
        }

        @Test
        @DisplayName("El builder reporta el tipo TRUE_FALSE")
        void reportsType() {
            assertEquals(QuestionType.TRUE_FALSE, builder.getQuestionType());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Validación de campos obligatorios")
    class CamposObligatorios {

        @Test
        @DisplayName("No se puede construir una pregunta sin enunciado")
        void cannotBuildWithoutStatement() {
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildCorrectAnswer("V");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @ParameterizedTest(name = "enunciado inválido [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "corto"})
        @DisplayName("Rechaza enunciados vacíos o muy cortos")
        void rejectsInvalidStatement(String statement) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildStatement(statement));
        }

        @Test
        @DisplayName("No se puede construir sin dificultad")
        void cannotBuildWithoutDifficulty() {
            builder.buildStatement(STATEMENT);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildCorrectAnswer("V");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("No se puede construir sin autor")
        void cannotBuildWithoutAuthor() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory(CATEGORY);
            builder.buildJustification(JUSTIFICATION);
            builder.buildCorrectAnswer("V");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("No se puede construir sin justificación")
        void cannotBuildWithoutJustification() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildCorrectAnswer("V");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Validación específica de Verdadero/Falso")
    class ValidacionEspecifica {

        @Test
        @DisplayName("No se puede construir sin indicar si es Verdadero o Falso")
        void mustHaveAnswer() {
            fillCommonFields();
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("respuesta correcta"));
        }

        @Test
        @DisplayName("El enunciado debe ser una afirmación (no puede terminar en '?')")
        void statementMustBeAffirmation() {
            fillValidQuestion();
            builder.buildStatement("¿La tesis es la idea principal de un texto?");
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("afirmación"));
        }

        @Test
        @DisplayName("Acepta que no se envíen opciones (son fijas)")
        void acceptsNullOptions() {
            fillCommonFields();
            assertDoesNotThrow(() -> builder.buildOptions(null));
            assertDoesNotThrow(() -> builder.buildOptions(List.of()));
            builder.buildCorrectAnswer("F");
            assertEquals(2, builder.build().getOptions().size());
        }

        @Test
        @DisplayName("Acepta las opciones Verdadero y Falso en cualquier orden y mayúsculas")
        void acceptsTrueFalseOptions() {
            assertDoesNotThrow(() -> builder.buildOptions(List.of("Verdadero", "Falso")));
            assertDoesNotThrow(() -> builder.buildOptions(List.of("falso", "VERDADERO")));
            assertDoesNotThrow(() -> builder.buildOptions(List.of(" Verdadero ", " Falso ")));
        }

        @Test
        @DisplayName("Rechaza opciones diferentes a Verdadero/Falso")
        void rejectsOtherOptions() {
            assertThrows(IllegalArgumentException.class, () -> builder.buildOptions(List.of("Sí", "No")));
        }

        @Test
        @DisplayName("Rechaza más de dos opciones")
        void rejectsThreeOptions() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("Verdadero", "Falso", "No sé")));
        }

        @Test
        @DisplayName("Rechaza una sola opción")
        void rejectsSingleOption() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("Verdadero")));
        }

        @Test
        @DisplayName("Rechaza opciones repetidas como Verdadero, Verdadero")
        void rejectsRepeatedTrue() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("Verdadero", "Verdadero")));
        }

        @Test
        @DisplayName("Rechaza opciones con elementos nulos")
        void rejectsNullElements() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(Arrays.asList("Verdadero", null)));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Respuestas válidas")
    class RespuestasValidas {

        @ParameterizedTest(name = "[{0}] se interpreta como Verdadero")
        @ValueSource(strings = {"Verdadero", "verdadero", "VERDADERO", "V", "v", "true", "TRUE", "A", " Verdadero "})
        @DisplayName("Variantes aceptadas para Verdadero")
        void trueVariants(String answer) {
            fillCommonFields();
            builder.buildCorrectAnswer(answer);
            assertEquals("Verdadero", builder.build().getCorrectOption().orElseThrow().getText());
        }

        @ParameterizedTest(name = "[{0}] se interpreta como Falso")
        @ValueSource(strings = {"Falso", "falso", "FALSO", "F", "f", "false", "FALSE", "B", " falso "})
        @DisplayName("Variantes aceptadas para Falso")
        void falseVariants(String answer) {
            fillCommonFields();
            builder.buildCorrectAnswer(answer);
            assertEquals("Falso", builder.build().getCorrectOption().orElseThrow().getText());
        }

        @ParameterizedTest(name = "respuesta inválida [{0}]")
        @ValueSource(strings = {"Sí", "No", "C", "Quizás", "1", "0", "verdad", "falsa"})
        @DisplayName("La respuesta debe corresponder a Verdadero o Falso")
        void rejectsInvalidAnswers(String answer) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer(answer));
        }

        @ParameterizedTest(name = "respuesta vacía [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Rechaza una respuesta vacía")
        void rejectsBlankAnswer(String answer) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer(answer));
        }

        @Test
        @DisplayName("La última respuesta definida es la que queda")
        void lastAnswerWins() {
            fillCommonFields();
            builder.buildCorrectAnswer("V");
            builder.buildCorrectAnswer("F");
            assertEquals("B", builder.build().getCorrectOption().orElseThrow().getLabel());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Reinicio y reutilización del builder")
    class Reutilizacion {

        @Test
        @DisplayName("Después de build() el builder queda reiniciado")
        void resetAfterBuild() {
            fillValidQuestion();
            builder.build();
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("reset() borra la respuesta correcta")
        void resetClearsAnswer() {
            fillValidQuestion();
            builder.reset();
            fillCommonFields();
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("Construye dos preguntas con respuestas distintas")
        void twoQuestions() {
            fillCommonFields();
            builder.buildCorrectAnswer("V");
            Question q1 = builder.build();
            fillCommonFields();
            builder.buildCorrectAnswer("F");
            Question q2 = builder.build();
            assertEquals("Verdadero", q1.getCorrectOption().orElseThrow().getText());
            assertEquals("Falso", q2.getCorrectOption().orElseThrow().getText());
        }
    }
}
