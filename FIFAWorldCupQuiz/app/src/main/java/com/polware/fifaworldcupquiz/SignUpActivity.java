package com.polware.fifaworldcupquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.polware.fifaworldcupquiz.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding bindingSignUp;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Register User");

        bindingSignUp = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(bindingSignUp.getRoot());

        bindingSignUp.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindingSignUp.buttonRegister.setClickable(false);
                String userEmail = bindingSignUp.editTextEmailRegisterUser.getText().toString();
                String userPassword = bindingSignUp.editTextPasswordRegisterUser.getText().toString();
                registerUser(userEmail, userPassword);
            }
        });

    }

    public void registerUser(String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Account created",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Log.w("createUser: failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Error registering the account",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}