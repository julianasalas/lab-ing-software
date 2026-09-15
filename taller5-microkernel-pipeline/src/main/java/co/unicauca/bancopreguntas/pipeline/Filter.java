package co.unicauca.bancopreguntas.pipeline;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionRequest;

public interface Filter {
    Question process(QuestionRequest request, Question currentQuestion) throws Exception;
}
