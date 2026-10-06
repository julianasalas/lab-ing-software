import static org.junit.jupiter.api.Assertions.*;

import builder.MultipleChoiceQuestionBuilder;
import builder.OpenQuestionBuilder;
import builder.QuestionBuilder;
import builder.TrueFalseQuestionBuilder;
import domain.DifficultyLevel;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Las reglas comunes viven en el Builder abstracto, así que deben cumplirse
 * igual en los tres Builders concretos. Cada prueba se ejecuta una vez por builder.
 */
@DisplayName("Reglas comunes de todos los Builders")
class QuestionBuilderCommonRulesTest {

    static Stream<QuestionBuilder> allBuilders() {
        return Stream.of(new MultipleChoiceQuestionBuilder(),
                new TrueFalseQuestionBuilder(),
                new OpenQuestionBuilder());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Un builder vacío no construye ninguna pregunta")
    void emptyBuilderFails(QuestionBuilder builder) {
        assertThrows(IllegalStateException.class, builder::build);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Rechaza enunciado nulo")
    void nullStatement(QuestionBuilder builder) {
        assertThrows(IllegalArgumentException.class, () -> builder.buildStatement(null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Rechaza enunciado de más de 1000 caracteres")
    void tooLongStatement(QuestionBuilder builder) {
        assertThrows(IllegalArgumentException.class, () -> builder.buildStatement("a".repeat(1001)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Rechaza dificultad nula")
    void nullDifficulty(QuestionBuilder builder) {
        assertThrows(IllegalArgumentException.class, () -> builder.buildDifficulty(null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Rechaza competencia vacía")
    void blankCategory(QuestionBuilder builder) {
        assertThrows(IllegalArgumentException.class, () -> builder.buildCategory(" "));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Rechaza autor nulo")
    void nullAuthor(QuestionBuilder builder) {
        assertThrows(IllegalArgumentException.class, () -> builder.buildAuthor(null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Rechaza justificación muy corta")
    void shortJustification(QuestionBuilder builder) {
        assertThrows(IllegalArgumentException.class, () -> builder.buildJustification("corta"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("La fuente es opcional y acepta null")
    void sourceAcceptsNull(QuestionBuilder builder) {
        assertDoesNotThrow(() -> builder.buildSource(null));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Sin enunciado no se construye aunque todo lo demás esté completo")
    void missingStatementOnly(QuestionBuilder builder) {
        builder.buildDifficulty(DifficultyLevel.LOW);
        builder.buildCategory("Lectura crítica");
        builder.buildAuthor("Juliana Salas");
        builder.buildJustification("Justificación válida y suficiente.");
        IllegalStateException e = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(e.getMessage().contains("enunciado"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBuilders")
    @DisplayName("Cada builder declara un tipo de pregunta")
    void declaresType(QuestionBuilder builder) {
        assertNotNull(builder.getQuestionType());
    }
}
