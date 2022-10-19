package com.polware.fifaworldcupquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polware.fifaworldcupquiz.databinding.ActivityQuizBinding;

public class QuizActivity extends AppCompatActivity {
    private ActivityQuizBinding bindingQuiz;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference().child("Questions");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = auth.getCurrentUser();
    private DatabaseReference referenceForScore = database.getReference();
    private String quizQuestion, correctAnswer, optionA, optionB, optionC, optionD;
    private int questionCount;
    private int questionNumber = 1;
    private String userAnswer;
    private int userCorrectAnswers = 0;
    private int userWrongAnswers = 0;
    private static final String TAG = "DATABASE_ERROR";
    private CountDownTimer countDownTimer;
    private static final long TOTAL_TIME = 10000;
    private long timeLeft = TOTAL_TIME;
    private boolean timerContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Running Quiz...");
        bindingQuiz = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(bindingQuiz.getRoot());
        quizWorldCup();

        bindingQuiz.buttonFinishQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveScore();
                startActivity(new Intent(QuizActivity.this, ScoreActivity.class));
                finish();
            }
        });

        bindingQuiz.buttonNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                enableAllAnswers();
                quizWorldCup();
            }
        });

        bindingQuiz.textViewAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                userAnswer = "a";
                if(correctAnswer.equals(userAnswer)) {
                    bindingQuiz.textViewAnswer1.setBackgroundResource(R.color.green);
                    bindingQuiz.textViewAnswer1.setTextColor(Color.WHITE);
                    userCorrectAnswers++;
                    bindingQuiz.textViewCorrectAnswers.setText(String.valueOf(userCorrectAnswers));
                }
                else {
                    bindingQuiz.textViewAnswer1.setBackgroundResource(R.color.orange);
                    bindingQuiz.textViewAnswer1.setTextColor(Color.WHITE);
                    userWrongAnswers++;
                    bindingQuiz.textViewWrongAnswers.setText(String.valueOf(userWrongAnswers));
                    markCorrectAnswer();
                }
            }
        });

        bindingQuiz.textViewAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                userAnswer = "b";
                if(correctAnswer.equals(userAnswer)) {
                    bindingQuiz.textViewAnswer2.setBackgroundResource(R.color.green);
                    bindingQuiz.textViewAnswer2.setTextColor(Color.WHITE);
                    userCorrectAnswers++;
                    bindingQuiz.textViewCorrectAnswers.setText(String.valueOf(userCorrectAnswers));
                }
                else {
                    bindingQuiz.textViewAnswer2.setBackgroundResource(R.color.orange);
                    bindingQuiz.textViewAnswer2.setTextColor(Color.WHITE);
                    userWrongAnswers++;
                    bindingQuiz.textViewWrongAnswers.setText(String.valueOf(userWrongAnswers));
                    markCorrectAnswer();
                }
            }
        });

        bindingQuiz.textViewAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                userAnswer = "c";
                if(correctAnswer.equals(userAnswer)) {
                    bindingQuiz.textViewAnswer3.setBackgroundResource(R.color.green);
                    bindingQuiz.textViewAnswer3.setTextColor(Color.WHITE);
                    userCorrectAnswers++;
                    bindingQuiz.textViewCorrectAnswers.setText(String.valueOf(userCorrectAnswers));
                }
                else {
                    bindingQuiz.textViewAnswer3.setBackgroundResource(R.color.orange);
                    bindingQuiz.textViewAnswer3.setTextColor(Color.WHITE);
                    userWrongAnswers++;
                    bindingQuiz.textViewWrongAnswers.setText(String.valueOf(userWrongAnswers));
                    markCorrectAnswer();
                }
            }
        });

        bindingQuiz.textViewAnswer4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                userAnswer = "d";
                if(correctAnswer.equals(userAnswer)) {
                    bindingQuiz.textViewAnswer4.setBackgroundResource(R.color.green);
                    bindingQuiz.textViewAnswer4.setTextColor(Color.WHITE);
                    userCorrectAnswers++;
                    bindingQuiz.textViewCorrectAnswers.setText(String.valueOf(userCorrectAnswers));
                }
                else {
                    bindingQuiz.textViewAnswer4.setBackgroundResource(R.color.orange);
                    bindingQuiz.textViewAnswer4.setTextColor(Color.WHITE);
                    userWrongAnswers++;
                    bindingQuiz.textViewWrongAnswers.setText(String.valueOf(userWrongAnswers));
                    markCorrectAnswer();
                }
            }
        });

    }

    public void quizWorldCup() {
        startTimer();
        bindingQuiz.textViewAnswer1.setBackgroundResource(R.color.background_color);
        bindingQuiz.textViewAnswer1.setTextColor(Color.BLACK);
        bindingQuiz.textViewAnswer2.setBackgroundResource(R.color.background_color);
        bindingQuiz.textViewAnswer2.setTextColor(Color.BLACK);
        bindingQuiz.textViewAnswer3.setBackgroundResource(R.color.background_color);
        bindingQuiz.textViewAnswer3.setTextColor(Color.BLACK);
        bindingQuiz.textViewAnswer4.setBackgroundResource(R.color.background_color);
        bindingQuiz.textViewAnswer4.setTextColor(Color.BLACK);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionCount = (int) dataSnapshot.getChildrenCount();
                quizQuestion = dataSnapshot.child(String.valueOf(questionNumber)).child("q").getValue().toString();
                optionA = dataSnapshot.child(String.valueOf(questionNumber)).child("a").getValue().toString();
                optionB = dataSnapshot.child(String.valueOf(questionNumber)).child("b").getValue().toString();
                optionC = dataSnapshot.child(String.valueOf(questionNumber)).child("c").getValue().toString();
                optionD = dataSnapshot.child(String.valueOf(questionNumber)).child("d").getValue().toString();
                correctAnswer = dataSnapshot.child(String.valueOf(questionNumber)).child("answer").getValue().toString();
                bindingQuiz.textViewQuestion.setText(quizQuestion);
                bindingQuiz.textViewAnswer1.setText(optionA);
                bindingQuiz.textViewAnswer2.setText(optionB);
                bindingQuiz.textViewAnswer3.setText(optionC);
                bindingQuiz.textViewAnswer4.setText(optionD);
                if(questionNumber < questionCount){
                    questionNumber++;
                }
                else {
                    Snackbar.make(bindingQuiz.constraintLayoutQuiz, "You have reached the last quiz question", Snackbar.LENGTH_LONG).show();
                    bindingQuiz.buttonNextQuestion.setClickable(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
                Snackbar.make(bindingQuiz.constraintLayoutQuiz, "Error reading database", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    public void markCorrectAnswer(){
        switch (correctAnswer){
            case "a":
                bindingQuiz.textViewAnswer1.setBackgroundResource(R.color.green);
                bindingQuiz.textViewAnswer1.setTextColor(Color.WHITE);
                break;
            case "b":
                bindingQuiz.textViewAnswer2.setBackgroundResource(R.color.green);
                bindingQuiz.textViewAnswer2.setTextColor(Color.WHITE);
                break;
            case "c":
                bindingQuiz.textViewAnswer3.setBackgroundResource(R.color.green);
                bindingQuiz.textViewAnswer3.setTextColor(Color.WHITE);
                break;
            case "d":
                bindingQuiz.textViewAnswer4.setBackgroundResource(R.color.green);
                bindingQuiz.textViewAnswer4.setTextColor(Color.WHITE);
                break;
            default:
                break;
        }
    }

    public void startTimer(){
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDown();
            }

            @Override
            public void onFinish() {
                timerContinue = false;
                pauseTimer();
                bindingQuiz.textViewQuestion.setText("Time is up!");
                disableAllAnswers();
            }
        }.start();
        timerContinue = true;
    }

    public void resetTimer(){
        timeLeft = TOTAL_TIME;
        updateCountDown();
    }

    public void updateCountDown(){
        int second = (int) (timeLeft / 1000) % 60;
        bindingQuiz.textViewTime.setText(String.valueOf(second));
    }

    public void pauseTimer(){
        countDownTimer.cancel();
        timerContinue = false;
    }

    public void disableAllAnswers(){
        bindingQuiz.textViewAnswer1.setVisibility(View.GONE);
        bindingQuiz.textViewAnswer2.setVisibility(View.GONE);
        bindingQuiz.textViewAnswer3.setVisibility(View.GONE);
        bindingQuiz.textViewAnswer4.setVisibility(View.GONE);
    }

    public void enableAllAnswers(){
        bindingQuiz.textViewAnswer1.setVisibility(View.VISIBLE);
        bindingQuiz.textViewAnswer2.setVisibility(View.VISIBLE);
        bindingQuiz.textViewAnswer3.setVisibility(View.VISIBLE);
        bindingQuiz.textViewAnswer4.setVisibility(View.VISIBLE);
    }

    public void saveScore(){
        String userID = firebaseUser.getUid();
        referenceForScore.child("Scores").child(userID).child("correct").setValue(userCorrectAnswers)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(QuizActivity.this, "Score saved successfully",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

        referenceForScore.child("Scores").child(userID).child("wrong").setValue(userWrongAnswers);
    }

}