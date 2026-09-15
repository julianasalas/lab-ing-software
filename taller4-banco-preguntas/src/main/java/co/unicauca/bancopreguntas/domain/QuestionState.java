package co.unicauca.bancopreguntas.domain;

/**
 * Estados del ciclo de vida de una pregunta contemplados en este taller.
 *
 * Capa: dominio.
 */
public enum QuestionState {

    BORRADOR("Borrador"),
    PENDIENTE_REVISION("Pendiente de revision"),
    ELIMINADA("Eliminada");

    private final String label;

    QuestionState(String label) {
        this.label = label;
    }

    /** Texto legible que se muestra en la interfaz grafica. */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
