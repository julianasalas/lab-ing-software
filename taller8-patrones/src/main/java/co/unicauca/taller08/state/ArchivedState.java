package co.unicauca.taller08.state;

/**
 * Archivada: la pregunta ya no se utiliza activamente. Es un estado final,
 * por eso no permite ninguna operación (hereda todas como no permitidas).
 */
public class ArchivedState extends AbstractQuestionState {

    @Override
    public String getName() {
        return "Archivada";
    }
}
