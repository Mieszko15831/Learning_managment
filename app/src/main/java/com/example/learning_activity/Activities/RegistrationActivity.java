package com.example.learning_activity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learning_activity.Models.User;
import com.example.learning_activity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailEDT, passEDT, confPassEDT;
    private Button zarejestrujBT;
    private ImageView backBT;
    private FirebaseAuth mAuth;
    private DatabaseReference mRootRef;
    private String emailString, passString, confPassString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEDT = findViewById(R.id.emailEDT);
        passEDT = findViewById(R.id.passEDT);
        confPassEDT = findViewById(R.id.confPassEDT);
        zarejestrujBT = findViewById(R.id.zalogujBT);
        backBT = findViewById(R.id.backBT);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        zarejestrujBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    zarejestruj();
                }
            }
        });
    }

    private boolean validate(){
        emailString = emailEDT.getText().toString();
        passString = passEDT.getText().toString();
        confPassString = confPassEDT.getText().toString();
        if(emailString.isEmpty()){
            emailEDT.setError("Enter E-mail");
            return false;
        }
        if(passString.isEmpty()){
            passEDT.setError("Enter password");
            return false;
        }
        if(!passString.equals(confPassString)){
            confPassEDT.setError("Different password");
            return false;
        }
        return true;
    }
    private void zarejestruj() {
        mAuth.createUserWithEmailAndPassword(emailString, passString)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if(task.isSuccessful()){
                                    String token = task.getResult();
                                    User user = new User(emailString,
                                            mAuth.getCurrentUser().getUid(),
                                            passString,
                                            token);

                                    mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(RegistrationActivity.this, "Zarejestrowano", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegistrationActivity.this, "Rejestracja nie powiodła się", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrationActivity.this, "Rejestracja niepowiodła się", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}