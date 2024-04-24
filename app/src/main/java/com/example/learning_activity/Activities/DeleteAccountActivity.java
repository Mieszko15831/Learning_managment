package com.example.learning_activity.Activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

public class DeleteAccountActivity extends AppCompatActivity {
    private Button deleteBT, confDeleteBT;
    private EditText passEDT;
    private ImageView backBT;
    private RelativeLayout confDeleteRL;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        handler = new Handler();

        deleteBT = findViewById(R.id.deleteBT);
        backBT = findViewById(R.id.backBT);
        confDeleteRL = findViewById(R.id.confDeleteRL);
        confDeleteBT = findViewById(R.id.confDeleteBT);
        passEDT = findViewById(R.id.passEDT);

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confDeleteRL.setVisibility(View.GONE);
                finish();
            }
        });

        deleteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confDeleteRL.setVisibility(View.VISIBLE);
            }
        });

        confDeleteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndStartDeleting();
            }
        });
    }

    private void reauthAndStartDeleting(String password){
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "User re-authenticated.");
                deleteUserAndAllTasks(user);
            }
        });
    }

    private void deleteUserAndAllTasks(FirebaseUser user){
        FirebaseDatabase.getInstance().getReference().child("subtasks").orderByChild("userId").equalTo(user.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    snapshot1.getRef().removeValue();
                }

                FirebaseDatabase.getInstance().getReference()
                        .child("tasks").orderByChild("userId").equalTo(user.getUid())
                        .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            snapshot1.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                mDatabase.removeValue();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("TAG", "User account deleted.");
                                    Intent intent = new Intent(DeleteAccountActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", e.getMessage());
                            }
                        });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateAndStartDeleting(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        User myUser = task.getResult().getValue(User.class);
                        System.out.println(myUser.getEmail());
                        String password = myUser.getPassword();

                        if(!passEDT.getText().toString().equals(password)){
                            passEDT.setError("Błędne hasło");
                        }
                        else{
                            reauthAndStartDeleting(password);
                        }
                    }
                });
            }
        }, 4000);
    }
}