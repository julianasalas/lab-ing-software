package builder;

import domain.Question;
import domain.QuestionType;
import java.util.List;

/**
 * Builder concreto para preguntas abiertas.
 *
 * Reglas específicas:
 * <ul>
 *   <li>Una pregunta abierta no tiene opciones de respuesta.</li>
 *   <li>Debe tener una respuesta de referencia (criterio de evaluación) de al
 *       menos 10 caracteres, que se define con buildCorrectAnswer.</li>
 * </ul>
 */
public class OpenQuestionBuilder extends QuestionBuilder {

    public static final int MIN_REFERENCE_ANSWER_LENGTH = 10;

    private String referenceAnswer;

    @Override
    protected void resetSpecificParts() {
        referenceAnswer = null;
    }

    @Override
    public void buildOptions(List<String> options) {
        if (options != null && !options.isEmpty()) {
            throw new IllegalArgumentException("Una pregunta abierta no admite opciones de respuesta");
        }
    }

    /** En una pregunta abierta la "respuesta correcta" es la respuesta de referencia. */
    @Override
    public void buildCorrectAnswer(String answer) {
        String value = requireText(answer, "La respuesta de referencia es obligatoria");
        if (value.length() < MIN_REFERENCE_ANSWER_LENGTH) {
            throw new IllegalArgumentException("La respuesta de referencia debe tener al menos "
                    + MIN_REFERENCE_ANSWER_LENGTH + " caracteres");
        }
        this.referenceAnswer = value;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.OPEN;
    }

    @Override
    protected void validateSpecificRules() {
        if (referenceAnswer == null) {
            throw new IllegalStateException("Una pregunta abierta debe tener una respuesta de referencia");
        }
    }

    @Override
    protected Question createQuestion() {
        return new Question(getQuestionType(), statement, difficulty, category, author,
                justification, source, List.of(), referenceAnswer);
    }
}
