package com.polware.fifaworldcupquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.polware.fifaworldcupquiz.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding bindingSplashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindingSplashScreen = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(bindingSplashScreen.getRoot());

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.splash_text_animation);
        bindingSplashScreen.textViewSplash1.startAnimation(animation);
        bindingSplashScreen.textViewSplash2.startAnimation(animation);
        bindingSplashScreen.textViewSplash3.startAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }
        }, 5000);
    }
}