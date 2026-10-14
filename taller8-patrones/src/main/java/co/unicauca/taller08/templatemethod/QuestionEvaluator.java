package co.unicauca.taller08.templatemethod;

import co.unicauca.taller08.domain.Option;
import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Template Method: proceso general de evaluación de una respuesta.
 *
 * evaluate() es el MÉTODO PLANTILLA (final): define los pasos y su orden,
 * y las subclases solo implementan lo que cambia según el tipo de pregunta.
 *
 *   1. Validar respuesta            -> validateCommon() fijo + validate() abstracto
 *   2. Obtener respuestas elegidas  -> getSelectedAnswers() fijo (usa el gancho normalizeAnswer)
 *   3. Comparar con las correctas   -> compareAnswers() abstracto
 *   4. Determinar resultado         -> valor booleano devuelto
 *   5. Generar retroalimentación    -> generateFeedback() gancho con implementación por defecto
 */
public abstract class QuestionEvaluator {

    private String lastFeedback = "";

    public final boolean evaluate(Question question, List<String> answers) {
        // 1. Validar respuesta
        validateCommon(question, answers);
        validate(question, answers);

        // 2. Obtener respuestas seleccionadas (como letras A, B, C...)
        List<String> selected = getSelectedAnswers(question, answers);

        // 3. Comparar con respuestas correctas
        boolean correct = compareAnswers(question, selected);

        // 4. Determinar resultado  /  5. Generar retroalimentación
        lastFeedback = generateFeedback(question, selected, correct);
        return correct;
    }

    /** Tipo de pregunta que sabe evaluar esta subclase. */
    public abstract QuestionType getSupportedType();

    /** Paso que cambia: reglas de la respuesta según el tipo de pregunta. */
    protected abstract void validate(Question question, List<String> answers);

    /** Paso que cambia: cómo se decide si la respuesta es correcta. */
    protected abstract boolean compareAnswers(Question question, List<String> answers);

    /** Retroalimentación de la última evaluación. */
    public String getLastFeedback() {
        return lastFeedback;
    }

    // ------------------------------------------------------------------
    // Pasos comunes (no cambian entre tipos de pregunta)
    // ------------------------------------------------------------------

    private void validateCommon(Question question, List<String> answers) {
        if (question == null) {
            throw new IllegalArgumentException("La pregunta no puede ser null");
        }
        if (question.getType() != getSupportedType()) {
            throw new IllegalArgumentException("Este evaluador solo evalúa preguntas de tipo "
                    + getSupportedType().getDisplayName());
        }
        if (!question.isAvailableForTests()) {
            throw new IllegalStateException("La pregunta no se puede usar en una prueba: está en estado "
                    + question.getStateName() + " y debe estar Publicada");
        }
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una respuesta");
        }
    }

    private List<String> getSelectedAnswers(Question question, List<String> answers) {
        List<String> labels = new ArrayList<>();
        for (String answer : answers) {
            String label = normalizeAnswer(question, answer);
            if (!labels.contains(label)) {
                labels.add(label);
            }
        }
        return labels;
    }

    /**
     * Gancho (hook): convierte lo que escribió el estudiante en la letra de
     * una opción. Acepta la letra o el texto de la opción. Las subclases
     * pueden aceptar otras formas (por ejemplo "V" o "F").
     */
    protected String normalizeAnswer(Question question, String answer) {
        return question.findOption(answer)
                .map(Option::getLabel)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La respuesta '" + answer + "' no corresponde a ninguna opción"));
    }

    /** Gancho (hook) con implementación por defecto. */
    protected String generateFeedback(Question question, List<String> selected, boolean correct) {
        if (correct) {
            return "¡Respuesta correcta!";
        }
        return "Respuesta incorrecta. La respuesta correcta es: " + describeCorrect(question);
    }

    protected String describeCorrect(Question question) {
        List<String> parts = new ArrayList<>();
        for (Option o : question.getOptions()) {
            if (o.isCorrect()) {
                parts.add(o.getLabel() + ") " + o.getText());
            }
        }
        return String.join(", ", parts);
    }
}
