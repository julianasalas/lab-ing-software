import static org.junit.jupiter.api.Assertions.*;

import builder.MultipleChoiceQuestionBuilder;
import builder.OpenQuestionBuilder;
import builder.QuestionBuilder;
import builder.TrueFalseQuestionBuilder;
import director.QuestionDirector;
import domain.DifficultyLevel;
import domain.Question;
import domain.QuestionType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Pruebas del Director: demuestra que el MISMO proceso general de construcción
 * produce diferentes tipos de pregunta según el Builder que se le entregue.
 */
@DisplayName("QuestionDirector")
class QuestionDirectorTest {

    private static final String JUSTIFICATION = "Justificación suficientemente larga para la pregunta.";

    private QuestionDirector director;

    @BeforeEach
    void setUp() {
        director = new QuestionDirector();
    }

    /** Builder concreto + datos propios de su tipo + tipo esperado. */
    static Stream<Arguments> buildersAndData() {
        return Stream.of(
                Arguments.of(new MultipleChoiceQuestionBuilder(),
                        "¿Cuál es el resultado de 7 x 8?",
                        List.of("54", "56", "58", "64"), "B", QuestionType.MULTIPLE_CHOICE),
                Arguments.of(new TrueFalseQuestionBuilder(),
                        "El número 56 es el resultado de multiplicar 7 por 8.",
                        List.of(), "Verdadero", QuestionType.TRUE_FALSE),
                Arguments.of(new OpenQuestionBuilder(),
                        "Explique el procedimiento para multiplicar 7 por 8.",
                        List.of(), "Sumar siete veces el número ocho da 56.", QuestionType.OPEN));
    }

    private Question constructWith(QuestionBuilder builder, String statement,
                                   List<String> options, String answer) {
        director.setBuilder(builder);
        return director.construct(statement, DifficultyLevel.MEDIUM, "Razonamiento cuantitativo",
                "Juliana Salas", JUSTIFICATION, "Banco de preguntas", options, answer);
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Construcción de diferentes productos")
    class DiferentesProductos {

        @ParameterizedTest(name = "{4}")
        @MethodSource("QuestionDirectorTest#buildersAndData")
        @DisplayName("El mismo proceso construye el tipo de pregunta de cada builder")
        void sameProcessDifferentProducts(QuestionBuilder builder, String statement,
                                          List<String> options, String answer, QuestionType expected) {
            Question q = constructWith(builder, statement, options, answer);
            assertEquals(expected, q.getType());
            assertEquals(statement, q.getStatement());
            assertEquals(DifficultyLevel.MEDIUM, q.getDifficulty());
            assertEquals("Razonamiento cuantitativo", q.getCategory());
            assertEquals("Juliana Salas", q.getAuthor());
            assertEquals("Banco de preguntas", q.getSource());
        }

        @Test
        @DisplayName("Un solo director construye los tres tipos cambiando únicamente el builder")
        void oneDirectorThreeTypes() {
            Question mc = constructWith(new MultipleChoiceQuestionBuilder(),
                    "¿Cuál es el resultado de 7 x 8?", List.of("54", "56", "58"), "56");
            Question tf = constructWith(new TrueFalseQuestionBuilder(),
                    "Siete por ocho es igual a cincuenta y seis.", null, "V");
            Question open = constructWith(new OpenQuestionBuilder(),
                    "Explique cómo se multiplica siete por ocho.", null, "Se suma 8 siete veces.");

            Set<QuestionType> types = new HashSet<>(List.of(mc.getType(), tf.getType(), open.getType()));
            assertEquals(3, types.size());
            assertEquals(3, mc.getOptions().size());
            assertEquals(2, tf.getOptions().size());
            assertEquals(0, open.getOptions().size());
        }

        @Test
        @DisplayName("Selección múltiple construida por el director tiene la opción correcta indicada")
        void multipleChoiceThroughDirector() {
            Question q = constructWith(new MultipleChoiceQuestionBuilder(),
                    "¿Cuál es el resultado de 7 x 8?", List.of("54", "56", "58", "64"), "B");
            assertEquals("56", q.getCorrectOption().orElseThrow().getText());
        }

        @Test
        @DisplayName("Verdadero/Falso construida por el director tiene la respuesta indicada")
        void trueFalseThroughDirector() {
            Question q = constructWith(new TrueFalseQuestionBuilder(),
                    "El número 56 es el resultado de multiplicar 7 por 8.", List.of(), "Falso");
            assertEquals("Falso", q.getCorrectOption().orElseThrow().getText());
        }

        @Test
        @DisplayName("Abierta construida por el director guarda la respuesta de referencia")
        void openThroughDirector() {
            Question q = constructWith(new OpenQuestionBuilder(),
                    "Explique el procedimiento para multiplicar 7 por 8.", List.of(),
                    "Sumar siete veces el número ocho.");
            assertEquals("Sumar siete veces el número ocho.", q.getReferenceAnswer());
        }

        @Test
        @DisplayName("constructWithoutSource construye la pregunta sin fuente")
        void constructWithoutSource() {
            director.setBuilder(new TrueFalseQuestionBuilder());
            Question q = director.constructWithoutSource(
                    "El agua hierve a 100 grados Celsius a nivel del mar.",
                    DifficultyLevel.LOW, "Ciencias", "Juliana Salas", JUSTIFICATION, null, "V");
            assertNull(q.getSource());
            assertEquals(QuestionType.TRUE_FALSE, q.getType());
        }

        @Test
        @DisplayName("El director puede construir muchas preguntas seguidas con el mismo builder")
        void manyQuestionsSameBuilder() {
            director.setBuilder(new MultipleChoiceQuestionBuilder());
            Set<String> ids = new HashSet<>();
            for (int i = 1; i <= 5; i++) {
                Question q = director.construct("Pregunta número " + i + " del banco", DifficultyLevel.LOW,
                        "Razonamiento cuantitativo", "Juliana Salas", JUSTIFICATION, null,
                        List.of("Uno", "Dos", "Tres"), "A");
                ids.add(q.getId());
            }
            assertEquals(5, ids.size());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Orden de construcción")
    class OrdenDeConstruccion {

        /** Builder espía: registra el orden en que el director llama a cada paso. */
        class RecordingBuilder extends MultipleChoiceQuestionBuilder {
            final List<String> calls = new ArrayList<>();

            @Override public void reset() { if (calls != null) calls.add("reset"); super.reset(); }
            @Override public void buildStatement(String s) { calls.add("statement"); super.buildStatement(s); }
            @Override public void buildDifficulty(DifficultyLevel d) { calls.add("difficulty"); super.buildDifficulty(d); }
            @Override public void buildCategory(String c) { calls.add("category"); super.buildCategory(c); }
            @Override public void buildAuthor(String a) { calls.add("author"); super.buildAuthor(a); }
            @Override public void buildJustification(String j) { calls.add("justification"); super.buildJustification(j); }
            @Override public void buildSource(String s) { calls.add("source"); super.buildSource(s); }
            @Override public void buildOptions(List<String> o) { calls.add("options"); super.buildOptions(o); }
            @Override public void buildCorrectAnswer(String a) { calls.add("answer"); super.buildCorrectAnswer(a); }
            @Override public Question build() { calls.add("build"); return super.build(); }
        }

        @Test
        @DisplayName("El director ejecuta los pasos siempre en el mismo orden")
        void callsStepsInOrder() {
            RecordingBuilder spy = new RecordingBuilder();
            spy.calls.clear();
            director.setBuilder(spy);
            director.construct("¿Cuál es el resultado de 7 x 8?", DifficultyLevel.LOW, "Matemáticas",
                    "Juliana Salas", JUSTIFICATION, null, List.of("54", "56", "58"), "B");
            // build() internamente vuelve a llamar reset(), por eso aparece al final
            assertIterableEquals(List.of("reset", "statement", "difficulty", "category", "author",
                    "justification", "source", "options", "answer", "build", "reset"), spy.calls);
        }

        @Test
        @DisplayName("El director reinicia el builder antes de construir (descarta datos previos)")
        void directorResetsBuilderFirst() {
            MultipleChoiceQuestionBuilder builder = new MultipleChoiceQuestionBuilder();
            builder.buildSource("Fuente vieja que no debe aparecer");
            director.setBuilder(builder);
            Question q = director.construct("¿Cuál es el resultado de 7 x 8?", DifficultyLevel.LOW,
                    "Matemáticas", "Juliana Salas", JUSTIFICATION, null, List.of("54", "56", "58"), "B");
            assertNull(q.getSource());
        }
    }

    // ------------------------------------------------------------------
    @Nested
    @DisplayName("Manejo del builder y errores")
    class ManejoDeErrores {

        @Test
        @DisplayName("No se puede construir sin haber asignado un builder")
        void constructWithoutBuilderFails() {
            assertThrows(IllegalStateException.class, () -> director.construct(
                    "Enunciado de prueba válido", DifficultyLevel.LOW, "Cat", "Autor",
                    JUSTIFICATION, null, List.of("a", "b", "c"), "A"));
        }

        @Test
        @DisplayName("No acepta un builder nulo en el constructor")
        void nullBuilderInConstructor() {
            assertThrows(IllegalArgumentException.class, () -> new QuestionDirector(null));
        }

        @Test
        @DisplayName("No acepta un builder nulo en setBuilder")
        void nullBuilderInSetter() {
            assertThrows(IllegalArgumentException.class, () -> director.setBuilder(null));
        }

        @Test
        @DisplayName("getBuilder devuelve el builder asignado")
        void getBuilderReturnsAssigned() {
            QuestionBuilder b = new OpenQuestionBuilder();
            QuestionDirector d = new QuestionDirector(b);
            assertSame(b, d.getBuilder());
            QuestionBuilder other = new TrueFalseQuestionBuilder();
            d.setBuilder(other);
            assertSame(other, d.getBuilder());
        }

        @Test
        @DisplayName("El director propaga la validación: sin enunciado no hay pregunta")
        void propagatesMissingStatement() {
            director.setBuilder(new MultipleChoiceQuestionBuilder());
            assertThrows(IllegalArgumentException.class, () -> director.construct(
                    null, DifficultyLevel.LOW, "Matemáticas", "Juliana Salas", JUSTIFICATION,
                    null, List.of("a", "b", "c"), "A"));
        }

        @Test
        @DisplayName("El director propaga la validación: respuesta que no corresponde a una opción")
        void propagatesInvalidAnswer() {
            director.setBuilder(new MultipleChoiceQuestionBuilder());
            assertThrows(IllegalArgumentException.class, () -> director.construct(
                    "¿Cuál es el resultado de 7 x 8?", DifficultyLevel.LOW, "Matemáticas",
                    "Juliana Salas", JUSTIFICATION, null, List.of("54", "56", "58"), "E"));
        }

        @Test
        @DisplayName("El director propaga la validación: selección múltiple sin opciones")
        void propagatesMissingOptions() {
            director.setBuilder(new MultipleChoiceQuestionBuilder());
            assertThrows(IllegalArgumentException.class, () -> director.construct(
                    "¿Cuál es el resultado de 7 x 8?", DifficultyLevel.LOW, "Matemáticas",
                    "Juliana Salas", JUSTIFICATION, null, List.of(), "A"));
        }

        @Test
        @DisplayName("El director propaga la validación: pregunta abierta con opciones")
        void propagatesOpenWithOptions() {
            director.setBuilder(new OpenQuestionBuilder());
            assertThrows(IllegalArgumentException.class, () -> director.construct(
                    "Explique el procedimiento de la multiplicación.", DifficultyLevel.LOW,
                    "Matemáticas", "Juliana Salas", JUSTIFICATION, null,
                    List.of("54", "56"), "Respuesta de referencia larga."));
        }

        @Test
        @DisplayName("Después de un error el director sigue funcionando con datos válidos")
        void worksAfterError() {
            director.setBuilder(new MultipleChoiceQuestionBuilder());
            assertThrows(IllegalArgumentException.class, () -> director.construct(
                    "¿Cuál es el resultado de 7 x 8?", DifficultyLevel.LOW, "Matemáticas",
                    "Juliana Salas", JUSTIFICATION, null, List.of("54", "56", "58"), "Z"));
            Question q = director.construct("¿Cuál es el resultado de 7 x 8?", DifficultyLevel.LOW,
                    "Matemáticas", "Juliana Salas", JUSTIFICATION, null, List.of("54", "56", "58"), "B");
            assertEquals("56", q.getCorrectOption().orElseThrow().getText());
        }
    }
}
