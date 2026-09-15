package co.unicauca.bancopreguntas.plugins;

import co.unicauca.bancopreguntas.core.*;
import java.util.UUID;

public class CaseQuestionPlugin implements QuestionPlugin {
    @Override
    public String getName() { return "Caso de Estudio"; }

    @Override
    public boolean supports(String type) {
        return "CASE_STUDY".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        Question q = new Question(UUID.randomUUID().toString(), "[Caso] " + request.getTitle(), request.getContent(), request.getType());
        q.setOptions(request.getOptions());
        q.setCorrectAnswer(request.getCorrectAnswer());
        q.setCategory(request.getCategory());
        q.setDifficulty(request.getDifficulty());
        return q;
    }
}
