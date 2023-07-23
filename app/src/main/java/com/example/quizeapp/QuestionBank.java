package com.example.quizeapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank  implements Parcelable {
    private List<Question> questions;
    private List<Question> questionsCopy;
    private int currentQuestionIndex;

    private int maxQuestionCount;
    public QuestionBank() {
        questions = new ArrayList<>();
        questionsCopy = new ArrayList<>();
        currentQuestionIndex = 0;
        maxQuestionCount = 20;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Question> getQuestionsCopy() {
        return questionsCopy;
    }

    public void addQuestionToCopy(Question question) {
        questionsCopy.add(question);
    }

    public void addQuestion(Question question) {
        if(questions.size() < maxQuestionCount) {
            questions.add(question);
        }
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    public void setMaxQuestionCount(int maxQuestionCount) {
        this.maxQuestionCount = maxQuestionCount;

        // Check if the current question index is beyond the new maxQuestionCount
        if (currentQuestionIndex >= maxQuestionCount) {
            // Reset the currentQuestionIndex to the last question within the new maxQuestionCount
            currentQuestionIndex = maxQuestionCount - 1;
        }

        // Remove any questions beyond the new maxQuestionCount from the question list
        while (questions.size() > maxQuestionCount) {
            questions.remove(questions.size() - 1);
        }
    }
    public int getMaxQuestionCount() {
        return maxQuestionCount;
    }

    public boolean hasNextQuestion() {
        return currentQuestionIndex < questions.size() - 1;
    }

    public void moveToNextQuestion() {
        if (hasNextQuestion()) {
            currentQuestionIndex++;
        }
    }

    public void resetCurrentQuestionIndex() {
        currentQuestionIndex = 0;
    }
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getQuestionCount() {
        return questions.size();
    }
    public void shuffleQuestions() {
        Collections.shuffle(questions);
        Collections.shuffle(questionsCopy);
    }

    // Parcelable creator
    public static final Creator<QuestionBank> CREATOR = new Creator<QuestionBank>() {
        @Override
        public QuestionBank createFromParcel(Parcel in) {
            return new QuestionBank(in);
        }

        @Override
        public QuestionBank[] newArray(int size) {
            return new QuestionBank[size];
        }
    };
    // Constructor that takes a Parcel and initializes the object from it
    protected QuestionBank(Parcel in) {
        currentQuestionIndex = in.readInt();
        questions = new ArrayList<>();
        questionsCopy = new ArrayList<>();
        in.readList(questions, Question.class.getClassLoader());
        in.readList(questionsCopy, Question.class.getClassLoader());
    }
    // Write object data to the parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(currentQuestionIndex);
        dest.writeList(questions);
        dest.writeList(questionsCopy);
    }
    // Describe the kind of special object
    // This is required, but you can use 0 if there are no special objects
    @Override
    public int describeContents() {
        return 0;
    }
}
