package co.unicauca.bancopreguntas.core;

public interface QuestionPlugin {
    String getName();
    boolean supports(String type);
    Question generate(QuestionRequest request) throws Exception;
}
