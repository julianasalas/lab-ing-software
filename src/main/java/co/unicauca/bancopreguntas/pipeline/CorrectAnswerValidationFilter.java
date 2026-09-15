package co.unicauca.bancopreguntas.pipeline;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionRequest;

public class CorrectAnswerValidationFilter implements Filter {
    @Override
    public Question process(QuestionRequest request, Question currentQuestion) throws Exception {
        if (request.getCorrectAnswer() == null || !request.getOptions().contains(request.getCorrectAnswer().trim())) {
            throw new Exception("Error Pipeline: La respuesta correcta debe pertenecer a las opciones enviadas.");
        }
        currentQuestion.setCorrectAnswer(request.getCorrectAnswer().trim());
        return currentQuestion;
    }
}
