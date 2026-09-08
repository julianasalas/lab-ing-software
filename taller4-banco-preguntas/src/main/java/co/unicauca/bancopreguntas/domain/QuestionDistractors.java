package co.unicauca.bancopreguntas.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Las cuatro opciones de respuesta de una pregunta de seleccion multiple.
 *
 * Capa: dominio.
 *
 * Es un objeto de valor inmutable: se separa de {@link Question} para cumplir el
 * principio de responsabilidad unica (SRP) — Question administra identidad y
 * estado; esta clase administra las opciones y su validez estructural.
 */
public final class QuestionDistractors {

    public static final int REQUIRED_OPTIONS = 4;
    private static final char FIRST_LETTER = 'A';

    private final List<String> options;

    /**
     * @param options exactamente cuatro opciones no vacias.
     * @throws IllegalArgumentException si no hay cuatro opciones o alguna esta vacia.
     */
    public QuestionDistractors(List<String> options) {
        if (options == null || options.size() != REQUIRED_OPTIONS) {
            throw new IllegalArgumentException(
                    "Una pregunta debe tener exactamente " + REQUIRED_OPTIONS + " opciones");
        }
        for (String option : options) {
            if (option == null || option.isBlank()) {
                throw new IllegalArgumentException("Las opciones no pueden estar vacias");
            }
        }
        this.options = Collections.unmodifiableList(List.copyOf(options));
    }

    public QuestionDistractors(String a, String b, String c, String d) {
        this(Arrays.asList(a, b, c, d));
    }

    /** Lista inmutable de las cuatro opciones. */
    public List<String> getOptions() {
        return options;
    }

    /** Opcion en la posicion indicada (0 = A, 1 = B, ...). */
    public String getOption(int index) {
        if (index < 0 || index >= REQUIRED_OPTIONS) {
            throw new IndexOutOfBoundsException("Opcion fuera de rango: " + index);
        }
        return options.get(index);
    }

    /** Letra correspondiente a una posicion: 0 -> 'A', 1 -> 'B', ... */
    public static char letterOf(int index) {
        if (index < 0 || index >= REQUIRED_OPTIONS) {
            throw new IndexOutOfBoundsException("Opcion fuera de rango: " + index);
        }
        return (char) (FIRST_LETTER + index);
    }

    /** true si el indice corresponde a una de las cuatro opciones. */
    public static boolean isValidIndex(int index) {
        return index >= 0 && index < REQUIRED_OPTIONS;
    }
}
