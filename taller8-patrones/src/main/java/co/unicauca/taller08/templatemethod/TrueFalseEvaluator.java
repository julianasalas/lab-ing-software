package co.unicauca.taller08.templatemethod;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import java.util.List;
import java.util.Locale;

/**
 * Verdadero/Falso: el estudiante selecciona Verdadero o Falso.
 * Acepta también "V", "F", "true" y "false".
 */
public class TrueFalseEvaluator extends QuestionEvaluator {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.TRUE_FALSE;
    }

    @Override
    protected void validate(Question question, List<String> answers) {
        if (answers.size() != 1) {
            throw new IllegalArgumentException("En una pregunta Verdadero/Falso debe seleccionar una sola respuesta");
        }
    }

    @Override
    protected String normalizeAnswer(Question question, String answer) {
        String value = answer == null ? "" : answer.trim().toLowerCase(Locale.ROOT);
        switch (value) {
            case "v", "true" -> value = "Verdadero";
            case "f", "false" -> value = "Falso";
            default -> value = answer;
        }
        return super.normalizeAnswer(question, value);
    }

    @Override
    protected boolean compareAnswers(Question question, List<String> answers) {
        return question.getCorrectLabels().contains(answers.get(0));
    }
}
