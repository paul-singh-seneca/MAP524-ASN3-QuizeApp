package com.example.quizeapp;

import java.util.ArrayList;

public class ResultManager {
    private  ArrayList<Result> results;
    
    private   int totalCorrectScore;
    private  int totalQuestions;

    public ResultManager(ArrayList<Result> userResults) {
        results = userResults;
        totalCorrectScore = 0;
        totalQuestions = 0;
    }
    
    public  int getTotalCorrectScore() {
        totalCorrectScore = 0;
        if(results != null && !results.isEmpty()) {
            for (Result result : results) {
                totalCorrectScore += result.getCorrectScore();
            }
        }
        return totalCorrectScore;
    }
    public  int getTotalQuestions(){
        totalQuestions = 0;
        if( results != null && !results.isEmpty()){
            for (Result result : results) {
                totalQuestions += result.getNoOfQuestions();
            }
        }
        return totalQuestions;
    }
    

}
