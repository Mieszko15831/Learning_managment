package com.example.learning_activity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.learning_activity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextView zarejestrujBT;
    private EditText emailEDT, passEDT;
    private Button loginBT;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEDT = findViewById(R.id.emailEDT);
        passEDT = findViewById(R.id.hasloEDT);
        loginBT = findViewById(R.id.zalogujBT);
        zarejestrujBT = findViewById(R.id.zarejestrujTV);

        mAuth = FirebaseAuth.getInstance();

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){
                    login();
                }
            }
        });

        zarejestrujBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateData(){
        if(emailEDT.getText().toString().isEmpty()){
            emailEDT.setError("Wpisz e-mail");
            return false;
        }

        if(passEDT.getText().toString().isEmpty()){
            passEDT.setError("Wpisz hasło");
            return false;
        }

        return true;
    }

    private void login(){
        String emailString = emailEDT.getText().toString().trim();
        String passString = passEDT.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(emailString, passString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Logowanie niepowiodło się", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}