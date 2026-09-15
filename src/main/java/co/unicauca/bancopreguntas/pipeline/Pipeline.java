package co.unicauca.bancopreguntas.pipeline;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionRequest;
import java.util.ArrayList;
import java.util.List;

public class Pipeline {
    private List<Filter> filters = new ArrayList<>();

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public Question execute(QuestionRequest request) throws Exception {
        Question currentQuestion = null;
        for (Filter filter : filters) {
            currentQuestion = filter.process(request, currentQuestion);
        }
        return currentQuestion;
    }
}
