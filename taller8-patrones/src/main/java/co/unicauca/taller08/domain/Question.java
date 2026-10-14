package co.unicauca.taller08.domain;

import co.unicauca.taller08.state.DraftState;
import co.unicauca.taller08.state.QuestionState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Pregunta del Banco de Preguntas Saber PRO.
 *
 * Es el CONTEXTO del patrón State: guarda internamente su estado y le
 * delega las operaciones del ciclo de vida. Así la clase no necesita
 * if/else para saber qué se puede hacer en cada estado.
 */
public class Question {

    private Long id;
    private String statement;
    private QuestionType type;
    private final List<Option> options = new ArrayList<>();
    private String reviewer;
    private String rejectionReason;
    private final List<String> stateHistory = new ArrayList<>();

    private QuestionState state;

    public Question() {
        state = new DraftState();
        stateHistory.add(state.getName());
    }

    public Question(String statement, QuestionType type) {
        this();
        this.statement = statement == null ? null : statement.trim();
        this.type = type;
    }

    // ------------------------------------------------------------------
    // Estado (patrón State)
    // ------------------------------------------------------------------

    public void setState(QuestionState state) {
        if (state == null) {
            throw new IllegalArgumentException("El estado no puede ser null");
        }
        this.state = state;
        stateHistory.add(state.getName());
    }

    public QuestionState getState() {
        return state;
    }

    public String getStateName() {
        return state.getName();
    }

    /** Borrador → Pendiente de revisión (la pregunta debe estar completa). */
    public void submitForReview() {
        state.submitForReview(this);
    }

    /** Pendiente de revisión → En revisión. */
    public void assignReviewer(String reviewer) {
        state.assignReviewer(this, reviewer);
    }

    /** En revisión → Aprobada. */
    public void approve() {
        state.approve(this);
    }

    /** En revisión → Rechazada. */
    public void reject(String reason) {
        state.reject(this, reason);
    }

    /** Rechazada → Borrador, para hacer los ajustes. */
    public void returnToDraft() {
        state.returnToDraft(this);
    }

    /** Aprobada → Publicada. */
    public void publish() {
        state.publish(this);
    }

    /** Publicada → Archivada. */
    public void archive() {
        state.archive(this);
    }

    /** Solo una pregunta publicada se puede usar en una prueba. */
    public boolean isAvailableForTests() {
        return state.isAvailableForTests();
    }

    public boolean isEditable() {
        return state.isEditable();
    }

    // ------------------------------------------------------------------
    // Contenido (solo se puede modificar en Borrador)
    // ------------------------------------------------------------------

    public void setStatement(String statement) {
        checkEditable();
        this.statement = statement == null ? null : statement.trim();
    }

    public void setType(QuestionType type) {
        checkEditable();
        this.type = type;
    }

    /**
     * Registra una opción de respuesta. La letra (A, B, C...) se asigna
     * automáticamente en el orden en que se agregan.
     */
    public Option addOption(String text, boolean correct) {
        checkEditable();
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("El texto de la opción es obligatorio");
        }
        for (Option o : options) {
            if (o.getText().equalsIgnoreCase(text.trim())) {
                throw new IllegalArgumentException("Ya existe la opción: " + text.trim());
            }
        }
        Option option = new Option(String.valueOf((char) ('A' + options.size())), text, correct);
        options.add(option);
        return option;
    }

    public void clearOptions() {
        checkEditable();
        options.clear();
    }

    private void checkEditable() {
        if (!state.isEditable()) {
            throw new IllegalStateException(
                    "Solo se puede modificar una pregunta en Borrador (estado actual: " + state.getName() + ")");
        }
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    public List<Option> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public List<String> getCorrectLabels() {
        List<String> labels = new ArrayList<>();
        for (Option o : options) {
            if (o.isCorrect()) {
                labels.add(o.getLabel());
            }
        }
        return labels;
    }

    /** Busca una opción por su letra o por su texto (sin importar mayúsculas). */
    public Optional<Option> findOption(String answer) {
        if (answer == null) {
            return Optional.empty();
        }
        String value = answer.trim().toLowerCase(Locale.ROOT);
        for (Option o : options) {
            if (o.getLabel().toLowerCase(Locale.ROOT).equals(value)
                    || o.getText().toLowerCase(Locale.ROOT).equals(value)) {
                return Optional.of(o);
            }
        }
        return Optional.empty();
    }

    public Long getId() {
        return id;
    }

    /** Lo asigna el repositorio al guardar la pregunta. */
    public void setId(Long id) {
        this.id = id;
    }

    public String getStatement() {
        return statement;
    }

    public QuestionType getType() {
        return type;
    }

    public String getReviewer() {
        return reviewer;
    }

    /** Lo usan los estados al asignar revisor. */
    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    /** Lo usan los estados al rechazar la pregunta. */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /** Estados por los que ha pasado la pregunta, en orden. */
    public List<String> getStateHistory() {
        return Collections.unmodifiableList(stateHistory);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(id == null ? "-" : id)
          .append(" [").append(state.getName()).append("] ")
          .append(type == null ? "(sin tipo)" : type.getDisplayName()).append('\n')
          .append("   ").append(statement).append('\n');
        for (Option o : options) {
            sb.append("     ").append(o).append('\n');
        }
        return sb.toString().stripTrailing();
    }
}
