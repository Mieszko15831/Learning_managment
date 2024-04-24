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

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText newEmailEDT, passEDT;
    private Button changeEmailBT;
    private ImageView backBT;
    private DatabaseReference mDatabase;
    private boolean isPasswordCorrect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        newEmailEDT = findViewById(R.id.emailEDT);
        passEDT = findViewById(R.id.passEDT);
        changeEmailBT = findViewById(R.id.changeEmailBT);
        backBT = findViewById(R.id.backBT);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        changeEmailBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    changeEmail();
                    finish();
                }
            }
        });

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean validate(){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(!passEDT.getText().toString().equals(user.getPassword())){
                    passEDT.setError("Niepoprawne hasło!");
                    isPasswordCorrect = false;
                }
                else isPasswordCorrect = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(newEmailEDT.getText().toString().isEmpty()){
            newEmailEDT.setError("Enter E-Mail");
            isPasswordCorrect = false;
        }

        if(passEDT.getText().toString().isEmpty()){
            passEDT.setError("Enter Password");
            isPasswordCorrect = false;
        }

        return isPasswordCorrect;
    }

    private void changeEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), passEDT.getText().toString());

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "User re-authenticated.");

                        user.updateEmail(newEmailEDT.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(ChangeEmailActivity.this, "Zaktualizowano E-Mail", Toast.LENGTH_SHORT).show();
                                            updateUser(newEmailEDT.getText().toString());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        newEmailEDT.setError(e.toString());
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        newEmailEDT.setError(e.toString());
                    }
                });

        /*if(user == null){
            Toast.makeText(this, "blabla", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();*/

    }

    private void updateUser(String email){
        mDatabase.get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    User user = task.getResult().getValue(User.class);
                    user.setEmail(email);
                    Map<String, Object> userValues = user.toMap();

                    mDatabase.updateChildren(userValues);
                }
            }
        });
    }
}