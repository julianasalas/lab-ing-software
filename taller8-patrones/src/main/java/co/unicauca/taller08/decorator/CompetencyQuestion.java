package co.unicauca.taller08.decorator;

/**
 * Pregunta asociada a una competencia Saber PRO.
 */
public class CompetencyQuestion extends QuestionDecorator {

    private final String competency;

    public CompetencyQuestion(QuestionComponent question, String competency) {
        super(question);
        if (competency == null || competency.isBlank()) {
            throw new IllegalArgumentException("La competencia es obligatoria");
        }
        this.competency = competency.trim();
    }

    @Override
    public String getDescription() {
        return question.getDescription() + " [COMPETENCIA: " + competency + "]";
    }
}
