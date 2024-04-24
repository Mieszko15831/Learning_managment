package com.example.learning_activity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.learning_activity.Models.User;
import com.example.learning_activity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPassEDT, newPassEDT, confPassEDT;
    private Button changePassBT;
    private ImageView backBT;
    private DatabaseReference mDatabase;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassEDT = findViewById(R.id.oldPassEDT);
        newPassEDT = findViewById(R.id.newPassEDT);
        confPassEDT = findViewById(R.id.confirmPassEDT);
        changePassBT = findViewById(R.id.changePassBT);
        backBT = findViewById(R.id.backBT);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePassBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    changePassword();
                    finish();
                }
            }
        });
    }

    private boolean validate(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                password = user.getPassword();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(oldPassEDT.getText().toString().isEmpty()){
            oldPassEDT.setError("Wpisz hasło");
            return false;
        }

        if(newPassEDT.getText().toString().isEmpty()){
            newPassEDT.setError("Wpisz hasło");
            return false;
        }

        if(!oldPassEDT.getText().toString().equals(password)){
            oldPassEDT.setError("Niepoprawne hasło");
            return false;
        }

        if(!newPassEDT.getText().toString().equals(confPassEDT.getText().toString())){
            confPassEDT.setError("Różne hasła");
            return false;
        }

        return true;
    }

    private void changePassword(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassEDT.getText().toString());
        user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("TAG", "User re-authenticated.");

                                user.updatePassword(newPassEDT.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(ChangePasswordActivity.this, "Zmieniono hasło", Toast.LENGTH_SHORT).show();
                                                    updateUser(newPassEDT.getText().toString());
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

    }

    private void updateUser(String newPassword){
        mDatabase.get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().getValue(User.class);
                    user.setPassword(newPassword);
                    Map<String, Object> userValues = user.toMap();

                    mDatabase.updateChildren(userValues);
                }
            }
        });
    }
}