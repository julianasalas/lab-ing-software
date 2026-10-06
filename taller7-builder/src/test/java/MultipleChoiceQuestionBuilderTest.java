import static org.junit.jupiter.api.Assertions.*;

import builder.MultipleChoiceQuestionBuilder;
import domain.DifficultyLevel;
import domain.Option;
import domain.Question;
import domain.QuestionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias del builder de preguntas de selección múltiple.
 */
@DisplayName("MultipleChoiceQuestionBuilder")
class MultipleChoiceQuestionBuilderTest {

    private static final String STATEMENT = "¿Cuánto es el 25% de 80.000?";
    private static final String CATEGORY = "Razonamiento cuantitativo";
    private static final String AUTHOR = "Juliana Salas";
    private static final String JUSTIFICATION = "El 25% equivale a la cuarta parte: 80.000 / 4 = 20.000.";
    private static final String SOURCE = "Guía Saber Pro - ICFES";
    private static final List<String> OPTIONS = List.of("15.000", "20.000", "25.000", "30.000");

    private MultipleChoiceQuestionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new MultipleChoiceQuestionBuilder();
    }

    /** Llena todos los campos comunes con valores válidos. */
    private void fillCommonFields() {
        builder.buildStatement(STATEMENT);
        builder.buildDifficulty(DifficultyLevel.MEDIUM);
        builder.buildCategory(CATEGORY);
        builder.buildAuthor(AUTHOR);
        builder.buildJustification(JUSTIFICATION);
        builder.buildSource(SOURCE);
    }

    /** Deja el builder con una pregunta completa y válida. */
    private void fillValidQuestion() {
        fillCommonFields();
        builder.buildOptions(OPTIONS);
        builder.buildCorrectAnswer("B");
    }

    private static List<String> options(int count) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add("Opción " + i);
        }
        return list;
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Construcción correcta")
    class ConstruccionCorrecta {

        @Test
        @DisplayName("Construye una pregunta válida de selección múltiple")
        void buildsValidQuestion() {
            fillValidQuestion();
            Question q = builder.build();
            assertNotNull(q);
            assertEquals(QuestionType.MULTIPLE_CHOICE, q.getType());
        }

        @Test
        @DisplayName("Conserva todos los datos comunes de la pregunta")
        void keepsCommonData() {
            fillValidQuestion();
            Question q = builder.build();
            assertAll(
                    () -> assertEquals(STATEMENT, q.getStatement()),
                    () -> assertEquals(DifficultyLevel.MEDIUM, q.getDifficulty()),
                    () -> assertEquals(CATEGORY, q.getCategory()),
                    () -> assertEquals(AUTHOR, q.getAuthor()),
                    () -> assertEquals(JUSTIFICATION, q.getJustification()),
                    () -> assertEquals(SOURCE, q.getSource()));
        }

        @Test
        @DisplayName("Etiqueta las opciones A, B, C, D en el orden recibido")
        void labelsOptionsInOrder() {
            fillValidQuestion();
            List<Option> opts = builder.build().getOptions();
            assertEquals(4, opts.size());
            assertEquals("A", opts.get(0).getLabel());
            assertEquals("15.000", opts.get(0).getText());
            assertEquals("B", opts.get(1).getLabel());
            assertEquals("C", opts.get(2).getLabel());
            assertEquals("D", opts.get(3).getLabel());
            assertEquals("30.000", opts.get(3).getText());
        }

        @Test
        @DisplayName("Marca exactamente una opción como correcta")
        void exactlyOneCorrectOption() {
            fillValidQuestion();
            Question q = builder.build();
            long correct = q.getOptions().stream().filter(Option::isCorrect).count();
            assertEquals(1, correct);
            assertEquals("B", q.getCorrectOption().orElseThrow().getLabel());
            assertEquals("20.000", q.getCorrectOption().orElseThrow().getText());
        }

        @Test
        @DisplayName("Permite indicar la respuesta correcta por el texto de la opción")
        void correctAnswerByText() {
            fillCommonFields();
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("25.000");
            assertEquals("C", builder.build().getCorrectOption().orElseThrow().getLabel());
        }

        @Test
        @DisplayName("La letra de la respuesta no distingue mayúsculas/minúsculas")
        void correctAnswerLetterIgnoresCase() {
            fillCommonFields();
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("d");
            assertEquals("D", builder.build().getCorrectOption().orElseThrow().getLabel());
        }

        @Test
        @DisplayName("El texto de la respuesta no distingue mayúsculas/minúsculas")
        void correctAnswerTextIgnoresCase() {
            fillCommonFields();
            builder.buildOptions(List.of("Lectura", "Escritura", "Inglés"));
            builder.buildCorrectAnswer("  INGLÉS ");
            assertEquals("C", builder.build().getCorrectOption().orElseThrow().getLabel());
        }

        @Test
        @DisplayName("Elimina los espacios sobrantes del enunciado y de las opciones")
        void trimsValues() {
            builder.buildStatement("   " + STATEMENT + "   ");
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory("  " + CATEGORY + " ");
            builder.buildAuthor(" " + AUTHOR + " ");
            builder.buildJustification("  " + JUSTIFICATION);
            builder.buildOptions(List.of("  uno ", "dos  ", " tres"));
            builder.buildCorrectAnswer("A");
            Question q = builder.build();
            assertEquals(STATEMENT, q.getStatement());
            assertEquals(CATEGORY, q.getCategory());
            assertEquals(AUTHOR, q.getAuthor());
            assertEquals(JUSTIFICATION, q.getJustification());
            assertEquals("uno", q.getOptions().get(0).getText());
            assertEquals("dos", q.getOptions().get(1).getText());
            assertEquals("tres", q.getOptions().get(2).getText());
        }

        @Test
        @DisplayName("La fuente es opcional: sin fuente queda en null")
        void sourceIsOptional() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.HIGH);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("A");
            assertNull(builder.build().getSource());
        }

        @ParameterizedTest(name = "fuente vacía [{0}] se guarda como null")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t"})
        @DisplayName("Una fuente vacía se interpreta como 'sin fuente'")
        void blankSourceIsNull(String source) {
            fillValidQuestion();
            builder.buildSource(source);
            assertNull(builder.build().getSource());
        }

        @Test
        @DisplayName("Una pregunta de selección múltiple no tiene respuesta de referencia")
        void noReferenceAnswer() {
            fillValidQuestion();
            assertNull(builder.build().getReferenceAnswer());
        }

        @Test
        @DisplayName("Cada pregunta construida recibe un id único y fecha de creación")
        void generatesIdAndDate() {
            fillValidQuestion();
            Question q1 = builder.build();
            fillValidQuestion();
            Question q2 = builder.build();
            assertNotNull(q1.getId());
            assertNotNull(q1.getCreatedAt());
            assertNotEquals(q1.getId(), q2.getId());
        }

        @Test
        @DisplayName("La lista de opciones de la pregunta no se puede modificar")
        void optionsAreUnmodifiable() {
            fillValidQuestion();
            Question q = builder.build();
            assertThrows(UnsupportedOperationException.class,
                    () -> q.getOptions().add(new Option("E", "otra", false)));
        }

        @Test
        @DisplayName("Modificar la lista original no altera la pregunta construida")
        void defensiveCopyOfOptions() {
            List<String> mutable = new ArrayList<>(OPTIONS);
            fillCommonFields();
            builder.buildOptions(mutable);
            builder.buildCorrectAnswer("A");
            mutable.clear();
            assertEquals(4, builder.build().getOptions().size());
        }

        @ParameterizedTest(name = "con {0} opciones")
        @ValueSource(ints = {3, 4, 5})
        @DisplayName("Acepta entre 3 y 5 opciones")
        void acceptsValidNumberOfOptions(int count) {
            fillCommonFields();
            builder.buildOptions(options(count));
            builder.buildCorrectAnswer("A");
            assertEquals(count, builder.build().getOptions().size());
        }

        @Test
        @DisplayName("Con 5 opciones la última se etiqueta E y puede ser la correcta")
        void fifthOptionIsE() {
            fillCommonFields();
            builder.buildOptions(options(5));
            builder.buildCorrectAnswer("E");
            Question q = builder.build();
            assertEquals("E", q.getOptions().get(4).getLabel());
            assertTrue(q.getOptions().get(4).isCorrect());
        }

        @ParameterizedTest(name = "dificultad {0}")
        @EnumSource(DifficultyLevel.class)
        @DisplayName("Acepta cualquier nivel de dificultad")
        void acceptsAnyDifficulty(DifficultyLevel level) {
            fillValidQuestion();
            builder.buildDifficulty(level);
            assertEquals(level, builder.build().getDifficulty());
        }

        @Test
        @DisplayName("El builder reporta el tipo MULTIPLE_CHOICE")
        void reportsType() {
            assertEquals(QuestionType.MULTIPLE_CHOICE, builder.getQuestionType());
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
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("A");
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("enunciado"));
        }

        @ParameterizedTest(name = "enunciado inválido [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "   ", "\n\t"})
        @DisplayName("Rechaza enunciados nulos o vacíos")
        void rejectsBlankStatement(String statement) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildStatement(statement));
        }

        @ParameterizedTest(name = "enunciado corto [{0}]")
        @ValueSource(strings = {"a", "¿Qué es?", "123456789", "   corto   "})
        @DisplayName("Rechaza enunciados de menos de 10 caracteres")
        void rejectsShortStatement(String statement) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildStatement(statement));
        }

        @Test
        @DisplayName("Acepta un enunciado de exactamente 10 caracteres")
        void acceptsStatementOfMinLength() {
            assertDoesNotThrow(() -> builder.buildStatement("1234567890"));
        }

        @Test
        @DisplayName("Acepta un enunciado de exactamente 1000 caracteres")
        void acceptsStatementOfMaxLength() {
            assertDoesNotThrow(() -> builder.buildStatement("x".repeat(1000)));
        }

        @Test
        @DisplayName("Rechaza un enunciado de más de 1000 caracteres")
        void rejectsTooLongStatement() {
            assertThrows(IllegalArgumentException.class, () -> builder.buildStatement("x".repeat(1001)));
        }

        @Test
        @DisplayName("No se puede construir sin dificultad")
        void cannotBuildWithoutDifficulty() {
            builder.buildStatement(STATEMENT);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("A");
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("dificultad"));
        }

        @Test
        @DisplayName("Rechaza una dificultad nula")
        void rejectsNullDifficulty() {
            assertThrows(IllegalArgumentException.class, () -> builder.buildDifficulty(null));
        }

        @Test
        @DisplayName("No se puede construir sin competencia (categoría)")
        void cannotBuildWithoutCategory() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("A");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @ParameterizedTest(name = "categoría inválida [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Rechaza una competencia vacía")
        void rejectsBlankCategory(String category) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildCategory(category));
        }

        @Test
        @DisplayName("No se puede construir sin autor")
        void cannotBuildWithoutAuthor() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory(CATEGORY);
            builder.buildJustification(JUSTIFICATION);
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("A");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @ParameterizedTest(name = "autor inválido [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Rechaza un autor vacío")
        void rejectsBlankAuthor(String author) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildAuthor(author));
        }

        @Test
        @DisplayName("No se puede construir sin justificación")
        void cannotBuildWithoutJustification() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildOptions(OPTIONS);
            builder.buildCorrectAnswer("A");
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @ParameterizedTest(name = "justificación inválida [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "corta", "123456789"})
        @DisplayName("Rechaza justificaciones vacías o de menos de 10 caracteres")
        void rejectsInvalidJustification(String justification) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildJustification(justification));
        }

        @Test
        @DisplayName("Un builder recién creado no puede construir nada")
        void emptyBuilderCannotBuild() {
            assertThrows(IllegalStateException.class, () -> builder.build());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Validación específica de selección múltiple")
    class ValidacionEspecifica {

        @Test
        @DisplayName("Una pregunta de selección múltiple debe tener opciones")
        void mustHaveOptions() {
            fillCommonFields();
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("opciones"));
        }

        @Test
        @DisplayName("Rechaza una lista de opciones nula")
        void rejectsNullOptions() {
            assertThrows(IllegalArgumentException.class, () -> builder.buildOptions(null));
        }

        @Test
        @DisplayName("Rechaza una lista de opciones vacía")
        void rejectsEmptyOptions() {
            assertThrows(IllegalArgumentException.class, () -> builder.buildOptions(List.of()));
        }

        @ParameterizedTest(name = "con {0} opciones")
        @ValueSource(ints = {1, 2, 6, 7, 10})
        @DisplayName("Rechaza menos de 3 o más de 5 opciones")
        void rejectsInvalidNumberOfOptions(int count) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildOptions(options(count)));
        }

        @ParameterizedTest(name = "opción vacía [{0}]")
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Rechaza opciones con texto vacío")
        void rejectsBlankOption(String blank) {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("Uno", blank, "Tres")));
        }

        @Test
        @DisplayName("Rechaza opciones nulas dentro de la lista")
        void rejectsNullOptionElement() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(Arrays.asList("Uno", null, "Tres")));
        }

        @Test
        @DisplayName("Rechaza opciones repetidas")
        void rejectsDuplicatedOptions() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("Uno", "Dos", "Uno")));
        }

        @Test
        @DisplayName("Considera repetidas opciones que solo cambian en mayúsculas o espacios")
        void rejectsDuplicatedOptionsIgnoringCaseAndSpaces() {
            assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("Bogotá", "Cali", "  BOGOTÁ ")));
        }

        @Test
        @DisplayName("No se puede construir sin respuesta correcta")
        void mustHaveCorrectAnswer() {
            fillCommonFields();
            builder.buildOptions(OPTIONS);
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("respuesta correcta"));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Respuestas válidas")
    class RespuestasValidas {

        @ParameterizedTest(name = "respuesta inexistente [{0}]")
        @ValueSource(strings = {"E", "F", "Z", "1", "AB", "40.000"})
        @DisplayName("La respuesta correcta debe corresponder a una opción existente")
        void answerMustMatchExistingOption(String answer) {
            fillCommonFields();
            builder.buildOptions(OPTIONS);
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> builder.buildCorrectAnswer(answer));
            assertTrue(e.getMessage().contains("no corresponde a una opción existente"));
        }

        @Test
        @DisplayName("Con 3 opciones la letra D no es una respuesta válida")
        void letterOutOfRange() {
            builder.buildOptions(options(3));
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer("D"));
        }

        @ParameterizedTest(name = "respuesta vacía [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  "})
        @DisplayName("Rechaza una respuesta correcta vacía")
        void rejectsBlankAnswer(String answer) {
            builder.buildOptions(OPTIONS);
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer(answer));
        }

        @Test
        @DisplayName("No se puede definir la respuesta antes que las opciones")
        void answerBeforeOptionsFails() {
            fillCommonFields();
            assertThrows(IllegalStateException.class, () -> builder.buildCorrectAnswer("A"));
        }

        @Test
        @DisplayName("Si se cambian las opciones se debe volver a definir la respuesta")
        void changingOptionsClearsAnswer() {
            fillValidQuestion();
            builder.buildOptions(List.of("Rojo", "Verde", "Azul"));
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("Una respuesta inválida no reemplaza la respuesta válida anterior")
        void invalidAnswerKeepsPreviousAnswer() {
            fillValidQuestion(); // respuesta B
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer("Z"));
            assertEquals("B", builder.build().getCorrectOption().orElseThrow().getLabel());
        }

        @Test
        @DisplayName("Definir de nuevo la respuesta reemplaza la anterior")
        void lastAnswerWins() {
            fillValidQuestion(); // respuesta B
            builder.buildCorrectAnswer("D");
            Question q = builder.build();
            assertEquals("D", q.getCorrectOption().orElseThrow().getLabel());
            assertFalse(q.getOptions().get(1).isCorrect());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Reinicio y reutilización del builder")
    class Reutilizacion {

        @Test
        @DisplayName("Después de build() el builder queda reiniciado")
        void builderIsResetAfterBuild() {
            fillValidQuestion();
            builder.build();
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("reset() borra los datos cargados")
        void resetClearsData() {
            fillValidQuestion();
            builder.reset();
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("Después de reset() la respuesta exige definir opciones de nuevo")
        void resetClearsOptions() {
            builder.buildOptions(OPTIONS);
            builder.reset();
            assertThrows(IllegalStateException.class, () -> builder.buildCorrectAnswer("A"));
        }

        @Test
        @DisplayName("El mismo builder construye varias preguntas independientes")
        void buildsSeveralIndependentQuestions() {
            fillValidQuestion();
            Question first = builder.build();

            builder.buildStatement("¿Cuál es la capital del departamento del Cauca?");
            builder.buildDifficulty(DifficultyLevel.LOW);
            builder.buildCategory("Competencias ciudadanas");
            builder.buildAuthor("Otro autor");
            builder.buildJustification("Popayán es la capital del Cauca.");
            builder.buildOptions(List.of("Pasto", "Popayán", "Cali"));
            builder.buildCorrectAnswer("Popayán");
            Question second = builder.build();

            assertNotSame(first, second);
            assertEquals(STATEMENT, first.getStatement());
            assertEquals(4, first.getOptions().size());
            assertEquals(3, second.getOptions().size());
            assertNull(second.getSource());
        }

        @Test
        @DisplayName("Tras un build fallido se puede completar el dato y construir")
        void canRecoverAfterFailedBuild() {
            fillCommonFields();
            builder.buildOptions(OPTIONS);
            assertThrows(IllegalStateException.class, () -> builder.build());
            builder.buildCorrectAnswer("A");
            assertNotNull(builder.build());
        }
    }
}
