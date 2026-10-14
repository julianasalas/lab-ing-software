package co.unicauca.taller08;

import co.unicauca.taller08.domain.Question;
import co.unicauca.taller08.domain.QuestionType;
import co.unicauca.taller08.service.QuestionValidator;

/**
 * Preguntas de ejemplo para las pruebas.
 */
final class TestQuestions {

    private TestQuestions() {
    }

    /** Única respuesta: A) 3  B) 4 (correcta)  C) 5 */
    static Question singleChoice() {
        Question q = new Question("¿Cuánto es 2 + 2 en aritmética básica?", QuestionType.SINGLE_CHOICE);
        q.addOption("3", false);
        q.addOption("4", true);
        q.addOption("5", false);
        return q;
    }

    /** Múltiples respuestas: A) 2 (c)  B) 4  C) 7 (c)  D) 9 */
    static Question multipleChoice() {
        Question q = new Question("¿Cuáles de los siguientes son números primos?", QuestionType.MULTIPLE_CHOICE);
        q.addOption("2", true);
        q.addOption("4", false);
        q.addOption("7", true);
        q.addOption("9", false);
        return q;
    }

    /** Verdadero/Falso con la respuesta indicada. */
    static Question trueFalse(boolean answer) {
        Question q = new Question("El agua hierve a 100 grados Celsius a nivel del mar.", QuestionType.TRUE_FALSE);
        QuestionValidator.addTrueFalseOptions(q, answer);
        return q;
    }

    /** Lleva la pregunta por todo el ciclo hasta Publicada. */
    static Question publish(Question q) {
        q.submitForReview();
        q.assignReviewer("Revisor");
        q.approve();
        q.publish();
        return q;
    }

    /** Lleva la pregunta hasta En revisión. */
    static Question toReview(Question q) {
        q.submitForReview();
        q.assignReviewer("Revisor");
        return q;
    }
}
