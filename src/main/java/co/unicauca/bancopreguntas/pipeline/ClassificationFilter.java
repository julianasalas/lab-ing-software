package co.unicauca.bancopreguntas.pipeline;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionRequest;

public class ClassificationFilter implements Filter {
    @Override
    public Question process(QuestionRequest request, Question currentQuestion) throws Exception {
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new Exception("Error Pipeline: Debe asignar una categoría/competencia.");
        }
        currentQuestion.setCategory(request.getCategory());
        currentQuestion.setDifficulty(request.getDifficulty() != null ? request.getDifficulty() : "MEDIA");
        return currentQuestion;
    }
}
