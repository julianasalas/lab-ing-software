package co.unicauca.taller08;

import static org.junit.jupiter.api.Assertions.*;

import co.unicauca.taller08.adapter.ExternalQuestionAdapter;
import co.unicauca.taller08.adapter.ExternalQuestionService;
import co.unicauca.taller08.adapter.QuestionProvider;
import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas del patrón Adapter: preguntas de un servicio externo.
 */
@DisplayName("Adapter - importar preguntas externas")
class ExternalQuestionAdapterTest {

    @Test
    @DisplayName("Ejemplo de la guía: la pregunta de la capital de Colombia")
    void guideExample() {
        QuestionProvider provider = new ExternalQuestionAdapter(new ExternalQuestionService());
        Question q = provider.getQuestion();
        assertEquals("¿Cuál es la capital de Colombia?", q.getStatement());
        assertEquals(1, q.getOptions().size());
        assertEquals("Bogotá", q.getOptions().get(0).getText());
        assertTrue(q.getOptions().get(0).isCorrect());
    }

    @Test
    @DisplayName("La pregunta importada es de única respuesta y queda en Borrador")
    void importedIsDraftSingleChoice() {
        Question q = new ExternalQuestionAdapter(new ExternalQuestionService()).getQuestion();
        assertEquals(QuestionType.SINGLE_CHOICE, q.getType());
        assertEquals("Borrador", q.getStateName());
        assertTrue(q.isEditable());
    }

    @Test
    @DisplayName("El adaptador implementa la interfaz que espera el banco")
    void implementsTarget() {
        assertInstanceOf(QuestionProvider.class, new ExternalQuestionAdapter(new ExternalQuestionService()));
    }

    @Test
    @DisplayName("La pregunta importada debe completarse antes de enviarla a revisión")
    void mustBeCompletedBeforeReview() {
        Question q = new ExternalQuestionAdapter(new ExternalQuestionService()).getQuestion();
        assertThrows(IllegalArgumentException.class, q::submitForReview);
        q.addOption("Medellín", false);
        q.addOption("Cali", false);
        q.submitForReview();
        assertEquals("Pendiente de revisión", q.getStateName());
    }

    @Test
    @DisplayName("Adapta otros datos del servicio externo")
    void otherData() {
        String json = "{\"statement\": \"¿Cuántos lados tiene un hexágono?\", \"answer\": \"Seis\"}";
        Question q = new ExternalQuestionAdapter(new ExternalQuestionService(json)).getQuestion();
        assertEquals("¿Cuántos lados tiene un hexágono?", q.getStatement());
        assertEquals("Seis", q.getCorrectLabels().isEmpty() ? null : q.getOptions().get(0).getText());
    }

    @Test
    @DisplayName("Soporta comillas escapadas dentro del texto")
    void escapedQuotes() {
        String json = "{\"statement\": \"¿Quién escribió \\\"Cien años de soledad\\\"?\", \"answer\": \"García Márquez\"}";
        Question q = new ExternalQuestionAdapter(new ExternalQuestionService(json)).getQuestion();
        assertEquals("¿Quién escribió \"Cien años de soledad\"?", q.getStatement());
    }

    @Test
    @DisplayName("El orden de los campos en el JSON no importa")
    void fieldOrder() {
        String json = "{\"answer\": \"Pacífico\", \"statement\": \"¿Qué océano baña a Buenaventura?\"}";
        Question q = new ExternalQuestionAdapter(new ExternalQuestionService(json)).getQuestion();
        assertEquals("¿Qué océano baña a Buenaventura?", q.getStatement());
        assertEquals("Pacífico", q.getOptions().get(0).getText());
    }

    @ParameterizedTest(name = "JSON inválido: {0}")
    @ValueSource(strings = {
            "",
            "   ",
            "{}",
            "{\"statement\": \"Pregunta sin respuesta\"}",
            "{\"answer\": \"Respuesta sin pregunta\"}",
            "{\"statement\": \"\", \"answer\": \"X\"}",
            "{\"statement\": \"Pregunta\", \"answer\": \"   \"}"})
    @DisplayName("Si faltan datos el adaptador lo informa")
    void invalidData(String json) {
        ExternalQuestionAdapter adapter = new ExternalQuestionAdapter(new ExternalQuestionService(json));
        assertThrows(IllegalArgumentException.class, adapter::getQuestion);
    }

    @Test
    @DisplayName("Si el servicio devuelve null el adaptador lo informa")
    void nullData() {
        ExternalQuestionAdapter adapter = new ExternalQuestionAdapter(new ExternalQuestionService(null));
        assertThrows(IllegalArgumentException.class, adapter::getQuestion);
    }

    @Test
    @DisplayName("No acepta un servicio null")
    void nullService() {
        assertThrows(IllegalArgumentException.class, () -> new ExternalQuestionAdapter(null));
    }

    @Test
    @DisplayName("El servicio externo devuelve el JSON tal cual (no se modifica)")
    void serviceReturnsJson() {
        String data = new ExternalQuestionService().getQuestion();
        assertTrue(data.contains("\"statement\""));
        assertTrue(data.contains("\"answer\""));
    }
}
