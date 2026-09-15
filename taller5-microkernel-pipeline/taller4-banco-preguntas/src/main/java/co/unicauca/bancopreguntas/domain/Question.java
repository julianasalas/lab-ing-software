package co.unicauca.bancopreguntas.domain;

import java.util.Objects;

/**
 * Entidad principal del dominio: una pregunta de seleccion multiple con
 * unica respuesta del banco de preguntas Saber Pro.
 *
 * Capa: dominio.
 */
public class Question {

    private final String id;
    private String name;
    private String statement;
    private QuestionDistractors distractors;
    private int correctAnswer;
    private QuestionState state;

    public Question(String id,
                    String name,
                    String statement,
                    QuestionDistractors distractors,
                    int correctAnswer,
                    QuestionState state) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la pregunta es obligatorio");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la pregunta es obligatorio");
        }
        if (statement == null || statement.isBlank()) {
            throw new IllegalArgumentException("El enunciado de la pregunta es obligatorio");
        }
        if (distractors == null) {
            throw new IllegalArgumentException("La pregunta debe tener opciones de respuesta");
        }
        if (!QuestionDistractors.isValidIndex(correctAnswer)) {
            throw new IllegalArgumentException("La respuesta correcta debe ser una de las cuatro opciones");
        }

        this.id = id;
        this.name = name;
        this.statement = statement;
        this.distractors = distractors;
        this.correctAnswer = correctAnswer;
        this.state = Objects.requireNonNullElse(state, QuestionState.BORRADOR);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public QuestionDistractors getDistractors() {
        return distractors;
    }

    public void setDistractors(QuestionDistractors distractors) {
        this.distractors = distractors;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        if (!QuestionDistractors.isValidIndex(correctAnswer)) {
            throw new IllegalArgumentException("La respuesta correcta debe ser una de las cuatro opciones");
        }
        this.correctAnswer = correctAnswer;
    }

    public QuestionState getState() {
        return state;
    }

    public void setState(QuestionState state) {
        if (state == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo");
        }
        this.state = state;
    }

    /** Letra de la respuesta correcta, para mostrar en la interfaz. */
    public char getCorrectAnswerLetter() {
        return QuestionDistractors.letterOf(correctAnswer);
    }

    /** Texto que se muestra en el comboBox de seleccion de pregunta. */
    @Override
    public String toString() {
        return id + " - " + name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Question)) {
            return false;
        }
        return id.equals(((Question) other).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
