package co.unicauca.taller08.adapter;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapter: convierte la pregunta del servicio externo (JSON con
 * "statement" y "answer") al modelo Question del Banco de Preguntas.
 *
 * El servicio externo solo trae la respuesta correcta, así que la pregunta
 * se crea como "selección múltiple con única respuesta" en estado Borrador,
 * con esa respuesta como opción correcta. Antes de enviarla a revisión el
 * docente debe agregar las demás opciones.
 */
public class ExternalQuestionAdapter implements QuestionProvider {

    private final ExternalQuestionService service;

    public ExternalQuestionAdapter(ExternalQuestionService service) {
        if (service == null) {
            throw new IllegalArgumentException("El servicio externo no puede ser null");
        }
        this.service = service;
    }

    @Override
    public Question getQuestion() {
        String data = service.getQuestion();

        // Transformar los datos externos al modelo interno Question
        String statement = readField(data, "statement");
        String answer = readField(data, "answer");

        Question question = new Question(statement, QuestionType.SINGLE_CHOICE);
        question.addOption(answer, true);
        return question;
    }

    /** Lee el valor de texto de un campo JSON simple: "campo": "valor". */
    static String readField(String json, String field) {
        if (json == null || json.isBlank()) {
            throw new IllegalArgumentException("El servicio externo no devolvió datos");
        }
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(field) + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("El dato externo no tiene el campo \"" + field + "\"");
        }
        String value = matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\").trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + field + "\" está vacío");
        }
        return value;
    }
}
