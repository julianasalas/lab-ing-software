package co.unicauca.taller08;

import static org.junit.jupiter.api.Assertions.*;

import co.unicauca.taller08.decorator.BasicQuestion;
import co.unicauca.taller08.decorator.CompetencyQuestion;
import co.unicauca.taller08.decorator.ContextualizedQuestion;
import co.unicauca.taller08.decorator.FeedbackQuestion;
import co.unicauca.taller08.decorator.HighDifficultyQuestion;
import co.unicauca.taller08.decorator.MarkedForReviewQuestion;
import co.unicauca.taller08.decorator.QuestionComponent;
import co.unicauca.taller08.decorator.QuestionDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas del patrón Decorator: características agregadas dinámicamente.
 */
@DisplayName("Decorator - características de las preguntas")
class QuestionDecoratorTest {

    private static final String TEXT = "¿Qué es arquitectura de software?";
    private QuestionComponent basic;

    @BeforeEach
    void setUp() {
        basic = new BasicQuestion(TEXT);
    }

    @Test
    @DisplayName("La pregunta básica vale 1.0 y no tiene marcas")
    void basicQuestion() {
        assertEquals(TEXT, basic.getDescription());
        assertEquals(1.0, basic.getScore());
    }

    @Test
    @DisplayName("Ejemplo de la guía: HighDifficultyQuestion agrega [ALTA DIFICULTAD]")
    void guideExample() {
        QuestionComponent question = new BasicQuestion(TEXT);
        question = new HighDifficultyQuestion(question);
        assertEquals(TEXT + " [ALTA DIFICULTAD]", question.getDescription());
        assertEquals(1.5, question.getScore());
    }

    @Test
    @DisplayName("Decorar no modifica el objeto original")
    void originalUnchanged() {
        new HighDifficultyQuestion(basic);
        assertEquals(TEXT, basic.getDescription());
        assertEquals(1.0, basic.getScore());
    }

    @Test
    @DisplayName("Contextualizada pone el contexto antes y suma 0.25")
    void contextualized() {
        QuestionComponent q = new ContextualizedQuestion(basic, "Lea el siguiente texto");
        assertEquals("[CONTEXTO: Lea el siguiente texto] " + TEXT, q.getDescription());
        assertEquals(1.25, q.getScore());
    }

    @Test
    @DisplayName("Con retroalimentación agrega la explicación y no cambia el puntaje")
    void feedback() {
        QuestionComponent q = new FeedbackQuestion(basic, "Ver capítulo 1");
        assertTrue(q.getDescription().endsWith("[RETROALIMENTACIÓN: Ver capítulo 1]"));
        assertEquals(1.0, q.getScore());
    }

    @Test
    @DisplayName("Asociada a competencia agrega la competencia")
    void competency() {
        QuestionComponent q = new CompetencyQuestion(basic, "Lectura crítica");
        assertTrue(q.getDescription().endsWith("[COMPETENCIA: Lectura crítica]"));
        assertEquals(1.0, q.getScore());
    }

    @Test
    @DisplayName("Marcada para revisión agrega la marca")
    void markedForReview() {
        QuestionComponent q = new MarkedForReviewQuestion(basic);
        assertEquals(TEXT + " [MARCADA PARA REVISIÓN]", q.getDescription());
        assertEquals(1.0, q.getScore());
    }

    @Test
    @DisplayName("Se pueden combinar todas las características")
    void combineAll() {
        QuestionComponent q = new HighDifficultyQuestion(basic);
        q = new ContextualizedQuestion(q, "Contexto");
        q = new FeedbackQuestion(q, "Explicación");
        q = new CompetencyQuestion(q, "Razonamiento cuantitativo");
        q = new MarkedForReviewQuestion(q);
        String d = q.getDescription();
        assertTrue(d.contains("[ALTA DIFICULTAD]"));
        assertTrue(d.startsWith("[CONTEXTO: Contexto]"));
        assertTrue(d.contains("[RETROALIMENTACIÓN: Explicación]"));
        assertTrue(d.contains("[COMPETENCIA: Razonamiento cuantitativo]"));
        assertTrue(d.endsWith("[MARCADA PARA REVISIÓN]"));
        assertEquals(1.75, q.getScore());
    }

    @Test
    @DisplayName("El mismo decorador se puede aplicar dos veces (los puntos se acumulan)")
    void sameDecoratorTwice() {
        QuestionComponent q = new HighDifficultyQuestion(new HighDifficultyQuestion(basic));
        assertEquals(2.0, q.getScore());
    }

    @Test
    @DisplayName("El orden de los decoradores cambia el orden de las marcas")
    void orderMatters() {
        String a = new MarkedForReviewQuestion(new HighDifficultyQuestion(basic)).getDescription();
        String b = new HighDifficultyQuestion(new MarkedForReviewQuestion(basic)).getDescription();
        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Un decorador es también un QuestionComponent")
    void decoratorIsComponent() {
        QuestionComponent q = new HighDifficultyQuestion(basic);
        assertInstanceOf(QuestionDecorator.class, q);
        assertInstanceOf(QuestionComponent.class, q);
    }

    @Test
    @DisplayName("BasicQuestion se puede crear desde una Question del banco")
    void fromQuestion() {
        BasicQuestion b = new BasicQuestion(TestQuestions.singleChoice());
        assertEquals("¿Cuánto es 2 + 2 en aritmética básica?", b.getDescription());
    }

    @Test
    @DisplayName("No se puede decorar null")
    void nullComponent() {
        assertThrows(IllegalArgumentException.class, () -> new HighDifficultyQuestion(null));
    }

    @ParameterizedTest(name = "texto [{0}]")
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("Los decoradores con texto exigen un valor")
    void textRequired(String text) {
        assertThrows(IllegalArgumentException.class, () -> new ContextualizedQuestion(basic, text));
        assertThrows(IllegalArgumentException.class, () -> new FeedbackQuestion(basic, text));
        assertThrows(IllegalArgumentException.class, () -> new CompetencyQuestion(basic, text));
        assertThrows(IllegalArgumentException.class, () -> new BasicQuestion(text));
    }
}
