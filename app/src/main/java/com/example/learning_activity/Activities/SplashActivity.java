package com.example.learning_activity.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.learning_activity.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }

        Handler handler = new Handler(); //Tworzy obiekt klasy Handler, który umożliwia planowanie zadań do wykonania w przyszłości

        // handler.postDelayer() planuje wykonanie zadania opisanego w bloku kodu Runnable po upływie 4000 milisekund.
        // W tym przypadku, blok kodu uruchamia nową aktywność (MainActivity) i kończy obecną aktywność (splash screen).
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAuth.getCurrentUser() != null){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    System.out.println(mAuth.getCurrentUser().getEmail());
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
                // Tworzy nową intencję (Intent) do uruchomienia nowej aktywności (LoginActivity)

            }
        }, 4000);
    }
}