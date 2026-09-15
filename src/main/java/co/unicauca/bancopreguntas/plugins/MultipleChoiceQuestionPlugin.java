package co.unicauca.bancopreguntas.plugins;

import co.unicauca.bancopreguntas.core.*;
import java.util.UUID;

public class MultipleChoiceQuestionPlugin implements QuestionPlugin {
    @Override
    public String getName() { return "Multiple Choice"; }

    @Override
    public boolean supports(String type) {
        return "MULTIPLE_CHOICE".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) {
        Question q = new Question(UUID.randomUUID().toString(), request.getTitle(), request.getContent(), request.getType());
        q.setOptions(request.getOptions());
        q.setCorrectAnswer(request.getCorrectAnswer());
        q.setCategory(request.getCategory());
        q.setDifficulty(request.getDifficulty());
        return q;
    }
}
