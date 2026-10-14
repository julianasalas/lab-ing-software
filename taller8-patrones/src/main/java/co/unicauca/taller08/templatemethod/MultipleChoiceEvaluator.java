package co.unicauca.taller08.templatemethod;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import java.util.HashSet;
import java.util.List;

/**
 * Selección múltiple: el estudiante puede seleccionar varias respuestas.
 * Es correcta solo si selecciona TODAS las correctas y ninguna incorrecta.
 */
public class MultipleChoiceEvaluator extends QuestionEvaluator {

    @Override
    public QuestionType getSupportedType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    protected void validate(Question question, List<String> answers) {
        if (answers.size() > question.getOptions().size()) {
            throw new IllegalArgumentException("Seleccionó más respuestas que opciones disponibles");
        }
    }

    @Override
    protected boolean compareAnswers(Question question, List<String> answers) {
        return new HashSet<>(answers).equals(new HashSet<>(question.getCorrectLabels()));
    }

    /** Además indica cuántas correctas acertó. */
    @Override
    protected String generateFeedback(Question question, List<String> selected, boolean correct) {
        if (correct) {
            return super.generateFeedback(question, selected, true);
        }
        List<String> correctLabels = question.getCorrectLabels();
        long hits = selected.stream().filter(correctLabels::contains).count();
        return "Respuesta incorrecta. Acertó " + hits + " de " + correctLabels.size()
                + " respuestas correctas. Las correctas son: " + describeCorrect(question);
    }
}
