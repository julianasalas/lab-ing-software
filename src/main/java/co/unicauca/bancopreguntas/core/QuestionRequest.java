package co.unicauca.bancopreguntas.core;

import java.util.List;

public class QuestionRequest {
    private String title;
    private String content;
    private String type;
    private List<String> options;
    private String correctAnswer;
    private String category;
    private String difficulty;

    public QuestionRequest(String title, String content, String type, List<String> options, 
                           String correctAnswer, String category, String difficulty) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getCategory() { return category; }
    public String getDifficulty() { return difficulty; }
}
