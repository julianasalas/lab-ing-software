package co.unicauca.taller08.adapter;

/**
 * Servicio de una institución externa (Adaptee). Entrega la pregunta como
 * texto JSON, en un formato distinto al del Banco de Preguntas, y no se
 * puede modificar.
 */
public class ExternalQuestionService {

    private final String json;

    public ExternalQuestionService() {
        this("""
            {
                "statement": "¿Cuál es la capital de Colombia?",
                "answer": "Bogotá"
            }
            """);
    }

    /** Permite simular otras respuestas del servicio externo. */
    public ExternalQuestionService(String json) {
        this.json = json;
    }

    public String getQuestion() {
        return json;
    }
}
