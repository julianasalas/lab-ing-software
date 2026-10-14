package co.unicauca.taller08.service;

import co.unicauca.taller08.domain.Option;
import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import java.util.ArrayList;
import java.util.List;

/**
 * Subsistema que valida que una pregunta esté completa según su tipo.
 *
 * Reglas:
 * - Enunciado obligatorio de al menos 10 caracteres y tipo obligatorio.
 * - Única respuesta: entre 3 y 5 opciones y exactamente 1 correcta.
 * - Múltiples respuestas: entre 3 y 6 opciones, al menos 2 correctas y no todas.
 * - Verdadero/Falso: exactamente las opciones Verdadero y Falso, 1 correcta.
 */
public class QuestionValidator {

    public static final int MIN_STATEMENT_LENGTH = 10;

    /** Lanza IllegalArgumentException con todos los errores encontrados. */
    public void validate(Question question) {
        List<String> errors = getErrors(question);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Pregunta inválida: " + String.join("; ", errors));
        }
    }

    public boolean isValid(Question question) {
        return getErrors(question).isEmpty();
    }

    public List<String> getErrors(Question question) {
        List<String> errors = new ArrayList<>();
        if (question == null) {
            errors.add("la pregunta no puede ser null");
            return errors;
        }
        String statement = question.getStatement();
        if (statement == null || statement.isBlank()) {
            errors.add("el enunciado es obligatorio");
        } else if (statement.length() < MIN_STATEMENT_LENGTH) {
            errors.add("el enunciado debe tener al menos " + MIN_STATEMENT_LENGTH + " caracteres");
        }
        if (question.getType() == null) {
            errors.add("debe seleccionar el tipo de pregunta");
            return errors;
        }
        int total = question.getOptions().size();
        int correct = question.getCorrectLabels().size();
        switch (question.getType()) {
            case SINGLE_CHOICE -> {
                if (total < 3 || total > 5) {
                    errors.add("una pregunta de única respuesta debe tener entre 3 y 5 opciones");
                }
                if (correct != 1) {
                    errors.add("una pregunta de única respuesta debe tener exactamente 1 respuesta correcta");
                }
            }
            case MULTIPLE_CHOICE -> {
                if (total < 3 || total > 6) {
                    errors.add("una pregunta de múltiples respuestas debe tener entre 3 y 6 opciones");
                }
                if (correct < 2) {
                    errors.add("una pregunta de múltiples respuestas debe tener al menos 2 respuestas correctas");
                } else if (correct == total) {
                    errors.add("no todas las opciones pueden ser correctas");
                }
            }
            case TRUE_FALSE -> {
                if (total != 2 || !hasTrueFalseOptions(question)) {
                    errors.add("una pregunta Verdadero/Falso debe tener exactamente las opciones Verdadero y Falso");
                }
                if (correct != 1) {
                    errors.add("una pregunta Verdadero/Falso debe tener exactamente 1 respuesta correcta");
                }
            }
            default -> errors.add("tipo de pregunta no soportado");
        }
        return errors;
    }

    private boolean hasTrueFalseOptions(Question question) {
        boolean hasTrue = false;
        boolean hasFalse = false;
        for (Option o : question.getOptions()) {
            hasTrue |= o.getText().equalsIgnoreCase("Verdadero");
            hasFalse |= o.getText().equalsIgnoreCase("Falso");
        }
        return hasTrue && hasFalse;
    }

    /** Ayuda para crear las opciones de una pregunta Verdadero/Falso. */
    public static void addTrueFalseOptions(Question question, boolean answerIsTrue) {
        if (question.getType() != QuestionType.TRUE_FALSE) {
            throw new IllegalArgumentException("La pregunta no es de tipo Verdadero/Falso");
        }
        question.addOption("Verdadero", answerIsTrue);
        question.addOption("Falso", !answerIsTrue);
    }
}
