package co.unicauca.taller08.decorator;

/**
 * Componente del patrón Decorator: lo que tienen en común la pregunta
 * básica y todas sus versiones decoradas.
 */
public interface QuestionComponent {

    String getDescription();

    double getScore();
}
