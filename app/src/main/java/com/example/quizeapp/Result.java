package com.example.quizeapp;

public class Result {
    private int correctScore;
    private int noOfQuestions;

    public Result(int correctScore, int noOfQuestions) {
        this.correctScore = correctScore;
        this.noOfQuestions = noOfQuestions;
    }

    public int getCorrectScore() {
        return correctScore;
    }
    public int getNoOfQuestions() {
        return noOfQuestions;
    }
}
