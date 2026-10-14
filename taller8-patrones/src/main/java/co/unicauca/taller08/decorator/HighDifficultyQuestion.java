package co.unicauca.taller08.decorator;

/**
 * Pregunta de alta dificultad: se marca en la descripción y suma 0.5 puntos.
 */
public class HighDifficultyQuestion extends QuestionDecorator {

    public static final double EXTRA_SCORE = 0.5;

    public HighDifficultyQuestion(QuestionComponent question) {
        super(question);
    }

    @Override
    public String getDescription() {
        return question.getDescription() + " [ALTA DIFICULTAD]";
    }

    @Override
    public double getScore() {
        return question.getScore() + EXTRA_SCORE;
    }
}
