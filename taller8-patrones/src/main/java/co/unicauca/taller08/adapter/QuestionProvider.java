package co.unicauca.taller08.adapter;

import co.unicauca.taller08.domain.Question;

/**
 * Interfaz que espera el Banco de Preguntas (Target del patrón Adapter).
 */
public interface QuestionProvider {

    Question getQuestion();
}
