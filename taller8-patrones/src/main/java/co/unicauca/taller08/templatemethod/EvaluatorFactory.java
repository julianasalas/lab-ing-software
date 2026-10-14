package co.unicauca.taller08.templatemethod;

import co.unicauca.taller08.domain.QuestionType;

/**
 * Entrega el evaluador que corresponde a cada tipo de pregunta.
 */
public class EvaluatorFactory {

    public QuestionEvaluator forType(QuestionType type) {
        if (type == null) {
            throw new IllegalArgumentException("La pregunta no tiene tipo");
        }
        return switch (type) {
            case SINGLE_CHOICE -> new SingleChoiceEvaluator();
            case MULTIPLE_CHOICE -> new MultipleChoiceEvaluator();
            case TRUE_FALSE -> new TrueFalseEvaluator();
        };
    }
}
