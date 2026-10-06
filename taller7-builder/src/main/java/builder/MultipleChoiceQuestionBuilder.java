package builder;

import domain.Option;
import domain.Question;
import domain.QuestionType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Builder concreto para preguntas de selección múltiple con única respuesta
 * (formato Saber Pro).
 *
 * Reglas específicas:
 * <ul>
 *   <li>Debe tener entre 3 y 5 opciones (etiquetadas A, B, C, D, E).</li>
 *   <li>Ninguna opción puede estar vacía y no se permiten opciones repetidas.</li>
 *   <li>Debe tener exactamente una respuesta correcta, que corresponda a una
 *       opción existente (se puede indicar por letra o por texto).</li>
 *   <li>Las opciones deben definirse antes que la respuesta correcta.</li>
 * </ul>
 */
public class MultipleChoiceQuestionBuilder extends QuestionBuilder {

    public static final int MIN_OPTIONS = 3;
    public static final int MAX_OPTIONS = 5;

    private List<String> optionTexts;
    private int correctIndex;

    @Override
    protected void resetSpecificParts() {
        optionTexts = new ArrayList<>();
        correctIndex = -1;
    }

    @Override
    public void buildOptions(List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Una pregunta de selección múltiple debe tener opciones");
        }
        if (options.size() < MIN_OPTIONS || options.size() > MAX_OPTIONS) {
            throw new IllegalArgumentException("Una pregunta de selección múltiple debe tener entre "
                    + MIN_OPTIONS + " y " + MAX_OPTIONS + " opciones");
        }
        List<String> cleaned = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String text : options) {
            String value = requireText(text, "Las opciones no pueden estar vacías");
            if (!seen.add(value.toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Opción repetida: " + value);
            }
            cleaned.add(value);
        }
        this.optionTexts = cleaned;
        // Si cambian las opciones, la respuesta correcta anterior deja de ser válida
        this.correctIndex = -1;
    }

    /**
     * @param answer letra de la opción (por ejemplo "B") o el texto exacto de
     *               la opción (sin importar mayúsculas).
     */
    @Override
    public void buildCorrectAnswer(String answer) {
        String value = requireText(answer, "La respuesta correcta es obligatoria");
        if (optionTexts.isEmpty()) {
            throw new IllegalStateException("Debe definir las opciones antes de la respuesta correcta");
        }
        int index = findOption(value);
        if (index < 0) {
            throw new IllegalArgumentException(
                    "La respuesta correcta '" + value + "' no corresponde a una opción existente");
        }
        this.correctIndex = index;
    }

    @Override
    public QuestionType getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    protected void validateSpecificRules() {
        if (optionTexts.isEmpty()) {
            throw new IllegalStateException("Una pregunta de selección múltiple debe tener opciones");
        }
        if (correctIndex < 0) {
            throw new IllegalStateException("Una pregunta de selección múltiple debe tener una respuesta correcta");
        }
    }

    @Override
    protected Question createQuestion() {
        List<Option> options = new ArrayList<>();
        for (int i = 0; i < optionTexts.size(); i++) {
            options.add(new Option(labelFor(i), optionTexts.get(i), i == correctIndex));
        }
        return new Question(getQuestionType(), statement, difficulty, category, author,
                justification, source, options, null);
    }

    private int findOption(String answer) {
        for (int i = 0; i < optionTexts.size(); i++) {
            if (labelFor(i).equalsIgnoreCase(answer) || optionTexts.get(i).equalsIgnoreCase(answer)) {
                return i;
            }
        }
        return -1;
    }

    private static String labelFor(int index) {
        return String.valueOf((char) ('A' + index));
    }
}
