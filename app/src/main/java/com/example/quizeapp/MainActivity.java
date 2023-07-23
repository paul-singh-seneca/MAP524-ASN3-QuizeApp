package com.example.quizeapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NumberPickerDialogFragment.OnNumberSelectedListener {
    private Button buttonTrue;
    private Button buttonFalse;
    private ProgressBar progressBar;
    private QuestionBank questionBank;
    private QuestionFragment questionFragment;

    private int correctAnswers;
    private int totalQuestions;
    private int selectedNoOfQuestions;
    private UserAttempts userAttempts;

    private static final String KEY_QUESTION_BANK = "QUESTION_BANK";
    private static final String KEY_CORRECT_ANSWERS = "CORRECT_ANSWERS";
    private static final String KEY_TOTAL_QUESTIONS = "TOTAL_QUESTIONS";
    private static final String KEY_USER_ATTEMPTS = "USER_ATTEMPTS";
    private static final String KEY_SELECTED_NO_OF_QUESTIONS = "SELECTED_NO_OF_QUESTIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonTrue = findViewById(R.id.buttonTrue);
        buttonFalse = findViewById(R.id.buttonFalse);
        progressBar = findViewById(R.id.progressBar);

        if(savedInstanceState != null){
            // resume state on rotation
            questionBank = savedInstanceState.getParcelable(KEY_QUESTION_BANK);
            userAttempts = savedInstanceState.getParcelable(KEY_USER_ATTEMPTS);
            correctAnswers = savedInstanceState.getInt(KEY_CORRECT_ANSWERS, 0);
            totalQuestions = savedInstanceState.getInt(KEY_TOTAL_QUESTIONS, 0);
            selectedNoOfQuestions = savedInstanceState.getInt(KEY_SELECTED_NO_OF_QUESTIONS, 0);
        }else{
            // initialize variables
            questionBank = new QuestionBank();
            correctAnswers = 0;
            totalQuestions = 0;
            selectedNoOfQuestions = 0;
            userAttempts = new UserAttempts();
        }

        loadData();
        updateQuestion();

        buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the necessary data to the Bundle
        // will be used to restore the state
        outState.putParcelable(KEY_QUESTION_BANK, questionBank);
        outState.putInt(KEY_CORRECT_ANSWERS, correctAnswers);
        outState.putInt(KEY_TOTAL_QUESTIONS, totalQuestions);
        outState.putInt(KEY_SELECTED_NO_OF_QUESTIONS, selectedNoOfQuestions);
        outState.putParcelable(KEY_USER_ATTEMPTS, userAttempts);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the saved data from the Bundle
        if (savedInstanceState != null) {
            questionBank = savedInstanceState.getParcelable(KEY_QUESTION_BANK);
            correctAnswers = savedInstanceState.getInt(KEY_CORRECT_ANSWERS);
            totalQuestions = savedInstanceState.getInt(KEY_TOTAL_QUESTIONS);
            userAttempts = savedInstanceState.getParcelable(KEY_USER_ATTEMPTS);
            selectedNoOfQuestions = savedInstanceState.getInt(KEY_SELECTED_NO_OF_QUESTIONS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fragment_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == MenuIds.ACTION_GET_AVERAGE){
            showAverage();// show average
            return true;
        }else if(id == MenuIds.ACTION_SELECT_NO_OF_QUESTIONS){
            showSelectNoOfQuestions();// select no of questions
            return true;
        }else if(id == MenuIds.ACTION_RESET_SAVED_RESULT){
            resetSavedResults(); // reset saved result
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void resetSavedResults() {
        // Delete the quiz_result.txt file to reset saved results
        File file = new File(getExternalFilesDir("/").getAbsolutePath() + "/results.txt");
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                showToast(getString(R.string.resultsReset));
            } else {
                showToast(getString(R.string.resetFailed));
            }
        } else {
            showToast(getString(R.string.noResults));
        }
    }

    private void showSelectNoOfQuestions() {
        NumberPickerDialogFragment dialogFragment = NumberPickerDialogFragment.newInstance(totalQuestions);
        dialogFragment.setOnNumberSelectedListener(this);
        dialogFragment.show(getSupportFragmentManager(), "number_picker_dialog");
    }


    @Override
    public void onNumberSelected(int number) {
        // Handle the selected number of questions
        if(number <= totalQuestions) {
            correctAnswers = 0;
            // Update the quiz UI to fit the new number of questions
            selectedNoOfQuestions = number;
            List<Question> questions = questionBank.getQuestionsCopy();
            Collections.shuffle(questions);

            questionFragment = new QuestionFragment();
            questionBank.setMaxQuestionCount(number);

            for (Question question : questions) {
                questionBank.addQuestion(question);
            }

            questionBank.resetCurrentQuestionIndex();
            showToast(getString(R.string.noOfQuestionsSet) + ":" + number);
        } else {
            showToast(getString(R.string.invalidNoOfQuestions));
        }
    }
    private void showAverage() {
        // Read the quiz result from the quiz_result.txt file
       ResultManager resultManager = new ResultManager( readQuizResult());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.resultAverage)
                .setMessage( getString(R.string.resultsText) + " " + resultManager.getTotalCorrectScore() + " / " + resultManager.getTotalQuestions() )
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                })
                .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                })
                .setCancelable(false)
                .show();
    }
    private ArrayList<Result> readQuizResult() {
        try {
            File file = new File(getExternalFilesDir("/").getAbsolutePath() + "/results.txt");
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            ArrayList<Result> results = new ArrayList<Result>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] questionData = line.split("\\|");
                if (questionData.length >= 2) {
                    int correctScore = Integer.parseInt(questionData[0]);
                    int noOfQuestions = Integer.parseInt(questionData[1]);
                    results.add( new Result( correctScore, noOfQuestions));
                }
            }
            bufferedReader.close();

            return results;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void updateQuestion() {
        questionFragment = new QuestionFragment();
        Question currentQuestion = questionBank.getCurrentQuestion();

        System.out.println(currentQuestion.getQuestionText() +" " + currentQuestion.getBackgroundColor());
        // configure question text and color
        Bundle bundle = new Bundle();
        bundle.putString("questionText", currentQuestion.getQuestionText());
        bundle.putString("questionBackground", currentQuestion.getBackgroundColor());
        questionFragment.setArguments(bundle);

        // Replace the current fragment with the new question fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, questionFragment);
        transaction.commit();
    }
    private void checkAnswer(boolean userAnswer) {
        Question currentQuestion = questionBank.getCurrentQuestion();

        if (currentQuestion.correctAnswer() == userAnswer) {
            correctAnswers++;
            userAttempts.addAttempt(true);
            showToast(getString(R.string.correctAnswer));
        } else {
            userAttempts.addAttempt(false);
           showToast(getString(R.string.wrongAnswer));
        }

        if (questionBank.hasNextQuestion()) {
            questionBank.moveToNextQuestion();
            updateQuestion();
        } else {
            // Quiz is finished, show results
            showQuizResultsDialog();
        }

        // Update progress bar
        int progress = (questionBank.getCurrentQuestionIndex() + 1) * 100 / questionBank.getQuestionCount();
        progressBar.setProgress(progress);
    }

    private void showQuizResultsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.resultsDialogTitle)
                .setMessage( getString(R.string.resultDialogText ) + " " + correctAnswers + " " + getString(R.string.outOf) + " " + questionBank.getQuestionCount())
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveQuizResult();
                        resetQuiz();
                    }
                })
                .setNegativeButton(R.string.ignore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetQuiz();
                    }
                })
                .setCancelable(false)
                .show();
    }
    private void saveQuizResult() {
        String result = correctAnswers + "|" + (selectedNoOfQuestions > 0? selectedNoOfQuestions: questionBank.getMaxQuestionCount()) +"\r\n";
        try {
            File file = new File(getExternalFilesDir("/").getAbsolutePath() + "/results.txt");
            FileOutputStream outputStream = new FileOutputStream(file,true);
            outputStream.write(result.getBytes());
            outputStream.close();
            showToast(getString(R.string.quizResultSaved));

        } catch (IOException e) {
            e.printStackTrace();
            showToast(getString(R.string.resultsNotSaved));
        }
    }


    private void resetQuiz() {
        selectedNoOfQuestions=0;
        correctAnswers = 0;
        questionBank.shuffleQuestions();
        questionBank.resetCurrentQuestionIndex();
        userAttempts = new UserAttempts();
        updateQuestion();
        progressBar.setProgress(0);
    }

    private void loadData() {
        try {
            Resources res = getResources();
            int totalLines = 0;

            // Get the array of question strings from the XML resource
            String[] questionsArray = res.getStringArray(R.array.questions);
            totalLines = questionsArray.length;

            totalQuestions = totalLines;
            questionBank.setMaxQuestionCount(totalLines);

            for (String questionData : questionsArray) {
                String[] questionAttributes = questionData.split("\\|");
                if (questionAttributes.length >= 3) {
                    String questionText = questionAttributes[0];
                    boolean correctAnswer = Boolean.parseBoolean(questionAttributes[1]);
                    String backgroundColor = questionAttributes[2];
                    Question question = new Question(questionText, correctAnswer, backgroundColor);
                    questionBank.addQuestion(question);
                    questionBank.addQuestionToCopy(question);
                }
            }

            showToast(getString(R.string.questionsLoaded));

        } catch (Exception e) {
            e.printStackTrace();
            showToast(getString(R.string.questionLoadFailed));
        }
    }

    private  void showToast(String message){
        Toast toast= Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
