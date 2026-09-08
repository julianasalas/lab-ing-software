package co.unicauca.bancopreguntas.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias de las entidades del dominio Question y QuestionDistractors.
 */
class QuestionTest {

    private static QuestionDistractors validDistractors() {
        return new QuestionDistractors("Opcion A", "Opcion B", "Opcion C", "Opcion D");
    }

    private static Question validQuestion() {
        return new Question("P-001", "Nombre", "Enunciado",
                validDistractors(), 1, QuestionState.BORRADOR);
    }

    @Test
    @DisplayName("una pregunta valida se construye correctamente")
    void construyePreguntaValida() {
        Question question = validQuestion();

        assertEquals("P-001", question.getId());
        assertEquals(QuestionState.BORRADOR, question.getState());
        assertEquals('B', question.getCorrectAnswerLetter());
    }

    @Test
    @DisplayName("el estado por defecto es Borrador cuando no se indica")
    void estadoPorDefectoEsBorrador() {
        Question question = new Question("P-002", "Nombre", "Enunciado",
                validDistractors(), 0, null);

        assertEquals(QuestionState.BORRADOR, question.getState());
    }

    @Test
    @DisplayName("no se admite una pregunta sin id")
    void rechazaIdVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Question("  ", "Nombre", "Enunciado",
                        validDistractors(), 0, QuestionState.BORRADOR));
    }

    @Test
    @DisplayName("no se admite una pregunta sin enunciado")
    void rechazaEnunciadoVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Question("P-001", "Nombre", "",
                        validDistractors(), 0, QuestionState.BORRADOR));
    }

    @Test
    @DisplayName("la respuesta correcta debe apuntar a una de las cuatro opciones")
    void rechazaRespuestaCorrectaFueraDeRango() {
        assertThrows(IllegalArgumentException.class,
                () -> new Question("P-001", "Nombre", "Enunciado",
                        validDistractors(), 4, QuestionState.BORRADOR));
    }

    @Test
    @DisplayName("setState rechaza un estado nulo")
    void setStateRechazaNulo() {
        Question question = validQuestion();

        assertThrows(IllegalArgumentException.class, () -> question.setState(null));
    }

    @Test
    @DisplayName("dos preguntas con el mismo id son iguales")
    void igualdadPorId() {
        Question first = validQuestion();
        Question second = new Question("P-001", "Otro nombre", "Otro enunciado",
                validDistractors(), 2, QuestionState.ELIMINADA);
        Question third = new Question("P-999", "Nombre", "Enunciado",
                validDistractors(), 0, QuestionState.BORRADOR);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
    }

    @Test
    @DisplayName("una pregunta debe tener exactamente cuatro opciones")
    void distractorsExigeCuatroOpciones() {
        List<String> tresOpciones = Arrays.asList("A", "B", "C");

        assertThrows(IllegalArgumentException.class,
                () -> new QuestionDistractors(tresOpciones));
    }

    @Test
    @DisplayName("las opciones no pueden estar vacias")
    void distractorsRechazaOpcionVacia() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuestionDistractors("A", "  ", "C", "D"));
    }

    @Test
    @DisplayName("letterOf traduce la posicion a la letra de la opcion")
    void letterOfTraducePosicion() {
        assertEquals('A', QuestionDistractors.letterOf(0));
        assertEquals('D', QuestionDistractors.letterOf(3));
        assertThrows(IndexOutOfBoundsException.class, () -> QuestionDistractors.letterOf(4));
    }

    @Test
    @DisplayName("la lista de opciones es inmutable")
    void listaDeOpcionesEsInmutable() {
        QuestionDistractors distractors = validDistractors();

        assertThrows(UnsupportedOperationException.class,
                () -> distractors.getOptions().add("Opcion E"));
    }
}
