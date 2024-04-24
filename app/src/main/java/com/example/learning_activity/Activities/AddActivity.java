package com.example.learning_activity.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.learning_activity.Models.Task;
import com.example.learning_activity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddActivity extends AppCompatActivity {


    private ImageView backBT;
    private EditText addedTaskNameEDT;
    private Button addBT;
    private ImageView micBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        backBT = findViewById(R.id.backFromAddBT);
        addBT = findViewById(R.id.dodajBT);
        addedTaskNameEDT = findViewById(R.id.addTaskNameEDT);
        micBT = findViewById(R.id.micIV);

        addBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    add();
                }
            }
        });

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        micBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechToText(view);
            }
        });
    }

    private void add() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("tasks");
        Task task = new Task(ref.push().getKey(),
                addedTaskNameEDT.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.child(task.getTaskId()).setValue(task);
        Intent intent = new Intent(AddActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate(){
        String addedTaskNameString = addedTaskNameEDT.getText().toString();
        if(addedTaskNameString.isEmpty()){
            addedTaskNameEDT.setError("Wpisz nazwę!");
            return false;
        }
        return true;
    }

    private void speechToText(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Powiedz coś");
        startActivityForResult(intent, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 111 & resultCode == RESULT_OK){
            addedTaskNameEDT.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }
}