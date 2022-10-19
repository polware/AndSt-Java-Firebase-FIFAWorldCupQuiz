package com.polware.fifaworldcupquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polware.fifaworldcupquiz.databinding.ActivityQuizBinding;
import com.polware.fifaworldcupquiz.databinding.ActivityScoreBinding;

public class ScoreActivity extends AppCompatActivity {
    private ActivityScoreBinding bindingScore;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = auth.getCurrentUser();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("Scores");
    private String correctAnswers;
    private String wrongAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Your Score");
        bindingScore = ActivityScoreBinding.inflate(getLayoutInflater());
        setContentView(bindingScore.getRoot());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userID = firebaseUser.getUid();
                correctAnswers = snapshot.child(userID).child("correct").getValue().toString();
                wrongAnswers = snapshot.child(userID).child("wrong").getValue().toString();
                bindingScore.textViewScoreCorrect.setText(correctAnswers);
                bindingScore.textViewScoreWrong.setText(wrongAnswers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bindingScore.buttonPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScoreActivity.this, MainActivity.class));
                finish();
            }
        });

        bindingScore.buttonExitScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}