package co.unicauca.bancopreguntas.core;

import java.util.HashMap;
import java.util.Map;

public class QuestionMicrokernel {
    private Map<String, Question> questions;
    private QuestionPluginManager pluginManager;

    public QuestionMicrokernel() {
        this.questions = new HashMap<>();
        this.pluginManager = new QuestionPluginManager();
    }

    public Question processAndAddQuestion(QuestionRequest request) throws Exception {
        QuestionPlugin plugin = pluginManager.getPluginForType(request.getType());
        if (plugin == null) {
            throw new IllegalArgumentException("No hay plugin registrado para el tipo: " + request.getType());
        }
        
        Question question = plugin.generate(request);
        questions.put(question.getId(), question);
        return question;
    }

    public Map<String, Question> getQuestions() {
        return questions;
    }
}
