package co.unicauca.taller08.decorator;

/**
 * Decorador abstracto: implementa la misma interfaz y guarda la pregunta
 * que envuelve. Por defecto le delega todo.
 */
public abstract class QuestionDecorator implements QuestionComponent {

    protected QuestionComponent question;

    public QuestionDecorator(QuestionComponent question) {
        if (question == null) {
            throw new IllegalArgumentException("La pregunta a decorar no puede ser null");
        }
        this.question = question;
    }

    @Override
    public String getDescription() {
        return question.getDescription();
    }

    @Override
    public double getScore() {
        return question.getScore();
    }
}
