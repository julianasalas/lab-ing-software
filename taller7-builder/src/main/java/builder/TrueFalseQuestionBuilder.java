package builder;

import domain.Option;
import domain.Question;
import domain.QuestionType;
import java.util.List;
import java.util.Locale;

/**
 * Builder concreto para preguntas de tipo Verdadero / Falso.
 *
 * Reglas específicas:
 * <ul>
 *   <li>Las opciones son siempre A) Verdadero y B) Falso; el builder las genera.
 *       Si el cliente envía opciones, solo se aceptan exactamente esas dos.</li>
 *   <li>La respuesta correcta es obligatoria y debe ser Verdadero o Falso
 *       (se acepta "Verdadero", "V", "true", "A", "Falso", "F", "false", "B").</li>
 *   <li>El enunciado debe ser una afirmación, por lo tanto no puede terminar en '?'.</li>
 * </ul>
 */
public class TrueFalseQuestionBuilder extends QuestionBuilder {

    public static final String TRUE_TEXT = "Verdadero";
    public static final String FALSE_TEXT = "Falso";

    private Boolean correctValue;

    @Override
    protected void resetSpecificParts() {
        correctValue = null;
    }

    @Override
    public void buildOptions(List<String> options) {
        if (options == null || options.isEmpty()) {
            return; // las opciones V/F son fijas, no hace falta enviarlas
        }
        boolean valid = options.size() == 2
                && containsIgnoreCase(options, TRUE_TEXT)
                && containsIgnoreCase(options, FALSE_TEXT);
        if (!valid) {
            throw new IllegalArgumentException(
                    "Una pregunta Verdadero/Falso solo admite las opciones Verdadero y Falso");
        }
    }

    @Override
    public void buildCorrectAnswer(String answer) {
        String value = requireText(answer, "La respuesta correcta es obligatoria")
                .toLowerCase(Locale.ROOT);
        switch (value) {
            case "verdadero", "v", "true", "a" -> correctValue = Boolean.TRUE;
            case "falso", "f", "false", "b" -> correctValue = Boolean.FALSE;
            default -> throw new IllegalArgumentException(
                    "La respuesta de una pregunta Verdadero/Falso debe ser Verdadero o Falso");
        }
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.TRUE_FALSE;
    }

    @Override
    protected void validateSpecificRules() {
        if (statement.endsWith("?")) {
            throw new IllegalStateException(
                    "El enunciado de una pregunta Verdadero/Falso debe ser una afirmación, no una pregunta");
        }
        if (correctValue == null) {
            throw new IllegalStateException("Una pregunta Verdadero/Falso debe tener una respuesta correcta");
        }
    }

    @Override
    protected Question createQuestion() {
        List<Option> options = List.of(
                new Option("A", TRUE_TEXT, correctValue),
                new Option("B", FALSE_TEXT, !correctValue));
        return new Question(getQuestionType(), statement, difficulty, category, author,
                justification, source, options, null);
    }

    private static boolean containsIgnoreCase(List<String> values, String expected) {
        return values.stream().anyMatch(v -> v != null && v.trim().equalsIgnoreCase(expected));
    }
}
