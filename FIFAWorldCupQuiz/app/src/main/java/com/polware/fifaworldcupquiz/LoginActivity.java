package com.polware.fifaworldcupquiz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.polware.fifaworldcupquiz.databinding.ActivityLoginBinding;

/**
 * Nota sobre el error: API Exception code 10
 * Se debe agregar la huella SHA1 del proyecto en la consola de Firebase
 * PASOS:
 * 1. Click on Gradle (From Right Side Panel, you will see Gradle Bar)
 * 2. Click on Refresh (Click on Refresh from Gradle Bar, you will see List Gradle scripts of your Project)
 * 3. Click on Your Project (Your Project Name form List (root))
 * 4. Click on Tasks
 * 5. Click on Android
 * 6. Double Click on signingReport (You will get SHA1 and MD5 in Run Bar(Sometimes it will be in Gradle Console))
 */

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding bindingLogin;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> activityResultLauncherForSignInGoogle;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Log into your account");

        bindingLogin = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bindingLogin.getRoot());
        registerActivityForSignInWithGoogle();

        bindingLogin.buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = bindingLogin.editTextEmail.getText().toString();
                String userPassword = bindingLogin.editTextPassword.getText().toString();
                loginFirebase(userEmail, userPassword);
            }
        });

        bindingLogin.linearLayoutGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        bindingLogin.textViewRegisterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        bindingLogin.textViewForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

    }

    public void loginFirebase(String email, String password) {
        bindingLogin.buttonSignIn.setClickable(false);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, "Login Successful",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Error: user or password not registered",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void signInWithGoogle() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        Intent signInGoogleIntent = googleSignInClient.getSignInIntent();
        activityResultLauncherForSignInGoogle.launch(signInGoogleIntent);
    }

    public void registerActivityForSignInWithGoogle(){
        activityResultLauncherForSignInGoogle = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //Receive data to saving in Database
                        int resultcode = result.getResultCode();
                        Intent data = result.getData();
                        if(resultcode == RESULT_OK && data != null){
                            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                                firebaseGoogleAccount(account);
                            }
                            catch (ApiException e){
                                Log.d(TAG, "onActivityResult | ApiException Code: " + e.getStatusCode());
                                Toast.makeText(LoginActivity.this, "Error: SignIn is not successful", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void firebaseGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "fireBaseAuthWithGoogle : " + account.getId());
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Sign In With Google Successfully",
                                Toast.LENGTH_LONG).show();
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        //String email = firebaseUser.getEmail();
                        if(firebaseUser != null){
                            Log.d(TAG, "onSuccess: User Signed In");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Login failed "+e.getMessage());
                    }
        });
    }

}