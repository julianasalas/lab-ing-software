package domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Producto del patrón Builder: una pregunta del Banco de Preguntas Saber Pro.
 *
 * La pregunta es inmutable. Solo los Builders (paquete builder) la crean,
 * después de haber validado todas las reglas de negocio.
 */
public final class Question {

    private final String id;
    private final QuestionType type;
    private final String statement;
    private final DifficultyLevel difficulty;
    private final String category;
    private final String author;
    private final String justification;
    private final String source;
    private final List<Option> options;
    private final String referenceAnswer;
    private final LocalDateTime createdAt;

    public Question(QuestionType type, String statement, DifficultyLevel difficulty,
                    String category, String author, String justification, String source,
                    List<Option> options, String referenceAnswer) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.statement = statement;
        this.difficulty = difficulty;
        this.category = category;
        this.author = author;
        this.justification = justification;
        this.source = source;
        this.options = options == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(options));
        this.referenceAnswer = referenceAnswer;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public QuestionType getType() {
        return type;
    }

    public String getStatement() {
        return statement;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getJustification() {
        return justification;
    }

    /** Fuente bibliográfica; es opcional, por eso puede ser null. */
    public String getSource() {
        return source;
    }

    /** Lista inmodificable de opciones (vacía para preguntas abiertas). */
    public List<Option> getOptions() {
        return options;
    }

    /** Respuesta de referencia / criterio de evaluación (solo preguntas abiertas). */
    public String getReferenceAnswer() {
        return referenceAnswer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** Opción marcada como correcta, si la pregunta es cerrada. */
    public Optional<Option> getCorrectOption() {
        return options.stream().filter(Option::isCorrect).findFirst();
    }

    public boolean hasOptions() {
        return !options.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------------------------------\n");
        sb.append("Tipo:          ").append(type.getDisplayName()).append('\n');
        sb.append("Enunciado:     ").append(statement).append('\n');
        sb.append("Dificultad:    ").append(difficulty.getDisplayName()).append('\n');
        sb.append("Competencia:   ").append(category).append('\n');
        sb.append("Autor:         ").append(author).append('\n');
        if (hasOptions()) {
            sb.append("Opciones:\n");
            for (Option option : options) {
                sb.append("   ").append(option).append('\n');
            }
        }
        if (referenceAnswer != null) {
            sb.append("Resp. ref.:    ").append(referenceAnswer).append('\n');
        }
        sb.append("Justificación: ").append(justification).append('\n');
        sb.append("Fuente:        ").append(source == null ? "(sin fuente)" : source).append('\n');
        sb.append("----------------------------------------------------------");
        return sb.toString();
    }
}
