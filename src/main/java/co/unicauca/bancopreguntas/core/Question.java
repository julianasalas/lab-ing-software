package co.unicauca.bancopreguntas.core;

import java.util.List;

public class Question {
    private String id;
    private String title;
    private String content;
    private String type;
    private List<String> options;
    private String correctAnswer;
    private String category;
    private String difficulty;

    public Question(String id, String title, String content, String type) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
}
