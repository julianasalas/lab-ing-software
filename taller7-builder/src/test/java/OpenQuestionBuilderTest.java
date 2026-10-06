import static org.junit.jupiter.api.Assertions.*;

import builder.OpenQuestionBuilder;
import domain.DifficultyLevel;
import domain.Question;
import domain.QuestionType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias del builder de preguntas abiertas.
 */
@DisplayName("OpenQuestionBuilder")
class OpenQuestionBuilderTest {

    private static final String STATEMENT = "Explique por qué el voto es un mecanismo de participación ciudadana.";
    private static final String CATEGORY = "Competencias ciudadanas";
    private static final String AUTHOR = "Juliana Salas";
    private static final String JUSTIFICATION = "Evalúa la comprensión de los mecanismos de participación.";
    private static final String REFERENCE = "Permite elegir representantes y decidir sobre asuntos públicos.";

    private OpenQuestionBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new OpenQuestionBuilder();
    }

    private void fillCommonFields() {
        builder.buildStatement(STATEMENT);
        builder.buildDifficulty(DifficultyLevel.HIGH);
        builder.buildCategory(CATEGORY);
        builder.buildAuthor(AUTHOR);
        builder.buildJustification(JUSTIFICATION);
    }

    private void fillValidQuestion() {
        fillCommonFields();
        builder.buildCorrectAnswer(REFERENCE);
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Construcción correcta")
    class ConstruccionCorrecta {

        @Test
        @DisplayName("Construye una pregunta abierta válida")
        void buildsValidQuestion() {
            fillValidQuestion();
            Question q = builder.build();
            assertEquals(QuestionType.OPEN, q.getType());
            assertEquals(STATEMENT, q.getStatement());
            assertEquals(DifficultyLevel.HIGH, q.getDifficulty());
            assertEquals(CATEGORY, q.getCategory());
            assertEquals(AUTHOR, q.getAuthor());
            assertEquals(JUSTIFICATION, q.getJustification());
        }

        @Test
        @DisplayName("Guarda la respuesta de referencia")
        void keepsReferenceAnswer() {
            fillValidQuestion();
            assertEquals(REFERENCE, builder.build().getReferenceAnswer());
        }

        @Test
        @DisplayName("Elimina espacios sobrantes en la respuesta de referencia")
        void trimsReferenceAnswer() {
            fillCommonFields();
            builder.buildCorrectAnswer("   " + REFERENCE + "   ");
            assertEquals(REFERENCE, builder.build().getReferenceAnswer());
        }

        @Test
        @DisplayName("Una pregunta abierta no tiene opciones ni opción correcta")
        void hasNoOptions() {
            fillValidQuestion();
            Question q = builder.build();
            assertTrue(q.getOptions().isEmpty());
            assertFalse(q.hasOptions());
            assertTrue(q.getCorrectOption().isEmpty());
        }

        @Test
        @DisplayName("Acepta una lista de opciones nula o vacía")
        void acceptsNoOptions() {
            fillValidQuestion();
            assertDoesNotThrow(() -> builder.buildOptions(null));
            assertDoesNotThrow(() -> builder.buildOptions(List.of()));
            assertNotNull(builder.build());
        }

        @Test
        @DisplayName("El enunciado de una pregunta abierta sí puede terminar en '?'")
        void statementCanBeAQuestion() {
            fillValidQuestion();
            builder.buildStatement("¿Por qué es importante la separación de poderes?");
            assertDoesNotThrow(() -> builder.build());
        }

        @Test
        @DisplayName("Conserva la fuente cuando se proporciona")
        void keepsSource() {
            fillValidQuestion();
            builder.buildSource("Constitución Política de Colombia, art. 258");
            assertEquals("Constitución Política de Colombia, art. 258", builder.build().getSource());
        }

        @Test
        @DisplayName("Acepta una respuesta de referencia de exactamente 10 caracteres")
        void acceptsMinLengthReference() {
            fillCommonFields();
            builder.buildCorrectAnswer("1234567890");
            assertEquals("1234567890", builder.build().getReferenceAnswer());
        }

        @Test
        @DisplayName("El builder reporta el tipo OPEN")
        void reportsType() {
            assertEquals(QuestionType.OPEN, builder.getQuestionType());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Validación de campos obligatorios")
    class CamposObligatorios {

        @Test
        @DisplayName("No se puede construir una pregunta sin enunciado")
        void cannotBuildWithoutStatement() {
            builder.buildDifficulty(DifficultyLevel.HIGH);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildCorrectAnswer(REFERENCE);
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("enunciado"));
        }

        @ParameterizedTest(name = "enunciado inválido [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "¿Por qué?"})
        @DisplayName("Rechaza enunciados vacíos o muy cortos")
        void rejectsInvalidStatement(String statement) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildStatement(statement));
        }

        @Test
        @DisplayName("No se puede construir sin competencia (categoría)")
        void cannotBuildWithoutCategory() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.HIGH);
            builder.buildAuthor(AUTHOR);
            builder.buildJustification(JUSTIFICATION);
            builder.buildCorrectAnswer(REFERENCE);
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        @DisplayName("No se puede construir sin justificación")
        void cannotBuildWithoutJustification() {
            builder.buildStatement(STATEMENT);
            builder.buildDifficulty(DifficultyLevel.HIGH);
            builder.buildCategory(CATEGORY);
            builder.buildAuthor(AUTHOR);
            builder.buildCorrectAnswer(REFERENCE);
            assertThrows(IllegalStateException.class, () -> builder.build());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Validación específica de preguntas abiertas")
    class ValidacionEspecifica {

        @Test
        @DisplayName("Una pregunta abierta no admite opciones de respuesta")
        void rejectsOptions() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> builder.buildOptions(List.of("A", "B", "C")));
            assertTrue(e.getMessage().contains("no admite opciones"));
        }

        @Test
        @DisplayName("Rechaza incluso una sola opción")
        void rejectsSingleOption() {
            assertThrows(IllegalArgumentException.class, () -> builder.buildOptions(List.of("Única")));
        }

        @Test
        @DisplayName("No se puede construir sin respuesta de referencia")
        void mustHaveReferenceAnswer() {
            fillCommonFields();
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> builder.build());
            assertTrue(e.getMessage().contains("respuesta de referencia"));
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Respuestas válidas")
    class RespuestasValidas {

        @ParameterizedTest(name = "respuesta vacía [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t\n"})
        @DisplayName("Rechaza una respuesta de referencia vacía")
        void rejectsBlankReference(String answer) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer(answer));
        }

        @ParameterizedTest(name = "respuesta corta [{0}]")
        @ValueSource(strings = {"Sí", "Porque sí", "123456789"})
        @DisplayName("Rechaza respuestas de referencia de menos de 10 caracteres")
        void rejectsShortReference(String answer) {
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer(answer));
        }

        @Test
        @DisplayName("Una respuesta inválida no reemplaza la anterior")
        void invalidReferenceKeepsPrevious() {
            fillValidQuestion();
            assertThrows(IllegalArgumentException.class, () -> builder.buildCorrectAnswer("No"));
            assertEquals(REFERENCE, builder.build().getReferenceAnswer());
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
        @DisplayName("reset() borra la respuesta de referencia")
        void resetClearsReference() {
            fillValidQuestion();
            builder.reset();
            fillCommonFields();
            assertThrows(IllegalStateException.class, () -> builder.build());
        }
    }
}
