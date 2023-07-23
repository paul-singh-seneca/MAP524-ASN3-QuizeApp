package com.example.quizeapp;

public class Question {
    private String questionText;
    private boolean correctAnswer;
    private String backgroundColor;

    public Question(String questionText, boolean correctAnswer, String backgroundColor) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.backgroundColor = backgroundColor;
    }

    public String getQuestionText() {
        return questionText;
    }

    public boolean correctAnswer() {
        return correctAnswer;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }
}

