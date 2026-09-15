package co.unicauca.bancopreguntas.pipeline;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionRequest;
import java.util.HashSet;

public class OptionsValidationFilter implements Filter {
    @Override
    public Question process(QuestionRequest request, Question currentQuestion) throws Exception {
        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new Exception("Error Pipeline: Debe haber mínimo 2 opciones.");
        }
        if (new HashSet<>(request.getOptions()).size() < request.getOptions().size()) {
            throw new Exception("Error Pipeline: No se permiten opciones duplicadas.");
        }
        currentQuestion.setOptions(request.getOptions());
        return currentQuestion;
    }
}
