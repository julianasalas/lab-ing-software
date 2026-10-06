package builder;

import domain.DifficultyLevel;
import domain.Question;
import domain.QuestionType;
import java.util.List;

/**
 * Builder abstracto del patrón Builder.
 *
 * Define los pasos para construir progresivamente una {@link Question} y
 * aplica las reglas de negocio comunes a todos los tipos de pregunta.
 * Los Builders concretos implementan los pasos que dependen del tipo
 * (opciones y respuesta correcta) y sus validaciones específicas.
 *
 * Reglas comunes:
 * <ul>
 *   <li>El enunciado es obligatorio y debe tener entre 10 y 1000 caracteres.</li>
 *   <li>La dificultad, la competencia (categoría) y el autor son obligatorios.</li>
 *   <li>La justificación es obligatoria y debe tener al menos 10 caracteres.</li>
 *   <li>La fuente es opcional.</li>
 * </ul>
 */
public abstract class QuestionBuilder {

    public static final int MIN_STATEMENT_LENGTH = 10;
    public static final int MAX_STATEMENT_LENGTH = 1000;
    public static final int MIN_JUSTIFICATION_LENGTH = 10;

    protected String statement;
    protected DifficultyLevel difficulty;
    protected String category;
    protected String author;
    protected String justification;
    protected String source;

    protected QuestionBuilder() {
        reset();
    }

    /** Deja el builder listo para construir una pregunta nueva. */
    public void reset() {
        statement = null;
        difficulty = null;
        category = null;
        author = null;
        justification = null;
        source = null;
        resetSpecificParts();
    }

    public void buildStatement(String statement) {
        String value = requireText(statement, "El enunciado es obligatorio");
        if (value.length() < MIN_STATEMENT_LENGTH) {
            throw new IllegalArgumentException(
                    "El enunciado debe tener al menos " + MIN_STATEMENT_LENGTH + " caracteres");
        }
        if (value.length() > MAX_STATEMENT_LENGTH) {
            throw new IllegalArgumentException(
                    "El enunciado no puede superar " + MAX_STATEMENT_LENGTH + " caracteres");
        }
        this.statement = value;
    }

    public void buildDifficulty(DifficultyLevel difficulty) {
        if (difficulty == null) {
            throw new IllegalArgumentException("La dificultad es obligatoria");
        }
        this.difficulty = difficulty;
    }

    public void buildCategory(String category) {
        this.category = requireText(category, "La competencia (categoría) es obligatoria");
    }

    public void buildAuthor(String author) {
        this.author = requireText(author, "El autor es obligatorio");
    }

    public void buildJustification(String justification) {
        String value = requireText(justification, "La justificación es obligatoria");
        if (value.length() < MIN_JUSTIFICATION_LENGTH) {
            throw new IllegalArgumentException(
                    "La justificación debe tener al menos " + MIN_JUSTIFICATION_LENGTH + " caracteres");
        }
        this.justification = value;
    }

    /** La fuente es opcional: null o vacío significa "sin fuente". */
    public void buildSource(String source) {
        this.source = (source == null || source.isBlank()) ? null : source.trim();
    }

    /**
     * Define las opciones de respuesta. Cada tipo de pregunta decide qué
     * opciones son válidas (o si no admite opciones).
     */
    public abstract void buildOptions(List<String> options);

    /**
     * Define la respuesta correcta. En preguntas cerradas debe corresponder a
     * una opción existente; en preguntas abiertas es la respuesta de referencia.
     */
    public abstract void buildCorrectAnswer(String answer);

    /** Tipo de pregunta que produce este builder. */
    public abstract QuestionType getQuestionType();

    /**
     * Valida todas las reglas de negocio y entrega la pregunta construida.
     * Después de construirla, el builder queda reiniciado para poder reutilizarse.
     *
     * @throws IllegalStateException si falta algún dato obligatorio o la
     *                               pregunta no cumple las reglas de su tipo.
     */
    public Question build() {
        validateCommonRules();
        validateSpecificRules();
        Question question = createQuestion();
        reset();
        return question;
    }

    /** Reinicia las partes propias de cada tipo de pregunta. */
    protected abstract void resetSpecificParts();

    /** Reglas de negocio propias del tipo de pregunta. */
    protected abstract void validateSpecificRules();

    /** Crea el producto con los datos ya validados. */
    protected abstract Question createQuestion();

    private void validateCommonRules() {
        if (statement == null) {
            throw new IllegalStateException("No se puede construir una pregunta sin enunciado");
        }
        if (difficulty == null) {
            throw new IllegalStateException("No se puede construir una pregunta sin dificultad");
        }
        if (category == null) {
            throw new IllegalStateException("No se puede construir una pregunta sin competencia (categoría)");
        }
        if (author == null) {
            throw new IllegalStateException("No se puede construir una pregunta sin autor");
        }
        if (justification == null) {
            throw new IllegalStateException("No se puede construir una pregunta sin justificación");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    /** Verifica que el texto no sea nulo ni vacío y lo devuelve sin espacios extremos. */
    protected static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
