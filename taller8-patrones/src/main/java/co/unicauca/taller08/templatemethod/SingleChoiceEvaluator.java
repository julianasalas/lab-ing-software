package co.unicauca.taller08.templatemethod;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import java.util.List;

/**
 * Selección única: el estudiante debe seleccionar exactamente una respuesta,
 * y debe ser la correcta.
 */
public class SingleChoiceEvaluator extends QuestionEvaluator {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.SINGLE_CHOICE;
    }

    @Override
    protected void validate(Question question, List<String> answers) {
        if (answers.size() != 1) {
            throw new IllegalArgumentException("En una pregunta de única respuesta debe seleccionar exactamente una opción");
        }
    }

    @Override
    protected boolean compareAnswers(Question question, List<String> answers) {
        return question.getCorrectLabels().contains(answers.get(0));
    }
}
