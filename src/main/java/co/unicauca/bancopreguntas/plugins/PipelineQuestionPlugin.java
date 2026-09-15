package co.unicauca.bancopreguntas.plugins;

import co.unicauca.bancopreguntas.core.*;
import co.unicauca.bancopreguntas.pipeline.*;

public class PipelineQuestionPlugin implements QuestionPlugin {
    private Pipeline pipeline;

    public PipelineQuestionPlugin() {
        this.pipeline = new Pipeline();
        pipeline.addFilter(new ContentValidationFilter());
        pipeline.addFilter(new OptionsValidationFilter());
        pipeline.addFilter(new ClassificationFilter());
        pipeline.addFilter(new CorrectAnswerValidationFilter());
    }

    @Override
    public String getName() { return "Pregunta Validada por Pipeline"; }

    @Override
    public boolean supports(String type) {
        return "PIPELINE_VALIDATED".equalsIgnoreCase(type);
    }

    @Override
    public Question generate(QuestionRequest request) throws Exception {
        return pipeline.execute(request);
    }
}
