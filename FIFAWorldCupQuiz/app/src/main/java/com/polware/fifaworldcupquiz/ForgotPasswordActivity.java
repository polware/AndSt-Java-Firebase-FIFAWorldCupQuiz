package com.polware.fifaworldcupquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.polware.fifaworldcupquiz.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding bindingForgotPassword;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Reset Password");
        bindingForgotPassword = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(bindingForgotPassword.getRoot());

        bindingForgotPassword.buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindingForgotPassword.progressBarResetPassword.setVisibility(View.VISIBLE);
                String email = bindingForgotPassword.editTextEmailForgotPassword.getText().toString();
                resetPassword(email);
            }
        });

    }

    public void resetPassword(String userEmail){
        auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Check your email inbox to continue the process",
                                    Toast.LENGTH_LONG);
                            bindingForgotPassword.buttonResetPassword.setClickable(false);
                            bindingForgotPassword.progressBarResetPassword.setVisibility(View.INVISIBLE);
                        }
                        else {
                            //Toast.makeText(ForgotPasswordActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error: Your email is invalid or not registered", Toast.LENGTH_LONG);
                            bindingForgotPassword.progressBarResetPassword.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

}