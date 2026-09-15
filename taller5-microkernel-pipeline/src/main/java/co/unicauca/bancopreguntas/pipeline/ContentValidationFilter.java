package co.unicauca.bancopreguntas.pipeline;

import co.unicauca.bancopreguntas.core.Question;
import co.unicauca.bancopreguntas.core.QuestionRequest;
import java.util.UUID;

public class ContentValidationFilter implements Filter {
    @Override
    public Question process(QuestionRequest request, Question currentQuestion) throws Exception {
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new Exception("Error Pipeline: El texto de la pregunta no puede estar vacío.");
        }
        if (request.getContent().length() < 10) {
            throw new Exception("Error Pipeline: La longitud mínima del texto debe ser de 10 caracteres.");
        }
        return new Question(UUID.randomUUID().toString(), request.getTitle(), request.getContent(), request.getType());
    }
}
