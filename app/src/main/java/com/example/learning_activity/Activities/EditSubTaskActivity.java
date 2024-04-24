package com.example.learning_activity.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditSubTaskActivity extends AppCompatActivity {

    private Button editSubTaskBT, deleteSubTaskBT;
    private EditText editSubTuskEDT;
    private Intent intent;
    private String subTaskName;
    private String taskId;
    private String dueDate;
    private TextView setDueDateTV;
    private ImageView backBT, micBT;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sub_task);

        editSubTaskBT = findViewById(R.id.editSubTaskBT);
        deleteSubTaskBT = findViewById(R.id.deleteSubTaskBT);
        editSubTuskEDT = findViewById(R.id.editSubTaskNameEDT);
        setDueDateTV = findViewById(R.id.setDueDateTV);
        backBT = findViewById(R.id.backFromSubtasksBT);
        micBT = findViewById(R.id.micIV);

        intent = getIntent();
        subTaskName = intent.getStringExtra("subTaskName"); // przyjmuje przekazaną wartość z SubTasksActivity
        dueDate = intent.getStringExtra("dueDate");
        taskId = intent.getStringExtra("taskId");

        editSubTuskEDT.setText(subTaskName);
        setDueDateTV.setText(dueDate);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("subtasks").child(taskId);

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editSubTaskBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSubTask();
                finish();
            }
        });

        setDueDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDueDate();
            }
        });

        deleteSubTaskBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSubTask();
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
            editSubTuskEDT.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }

    private void deleteSubTask(){
        mDatabase.removeValue();
    }

    private void updateSubTask(){
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    SubTask subTask = task.getResult().getValue(SubTask.class);

                    subTask.setName(editSubTuskEDT.getText().toString());
                    subTask.setDueDate(dueDate);
                    Map<String, Object> subTaskValues = subTask.toMap();


                    mDatabase.updateChildren(subTaskValues);
                }
            }
        });
        finish();
    }

    private void setDueDate(){
        Calendar calendar = Calendar.getInstance();

        int MONTH = calendar.get(Calendar.MONTH);
        int YEAR = calendar.get(Calendar.YEAR);
        int DAY = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditSubTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                month = month + 1;

                String formattedDay = String.format("%02d", dayOfMonth);
                String formattedMonth = String.format("%02d", month);
                setDueDateTV.setText(formattedDay + "/" + formattedMonth + "/" + year);
                dueDate = formattedDay + "/" + formattedMonth + "/" + year;
            }
        }, YEAR, MONTH, DAY);
        datePickerDialog.show();
    }
}