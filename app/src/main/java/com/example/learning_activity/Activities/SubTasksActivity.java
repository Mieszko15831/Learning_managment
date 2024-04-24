package com.example.learning_activity.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.learning_activity.Adapters.SubTaskAdapter;
import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SubTasksActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ConstraintLayout subTaskCL;
    private RecyclerView subTasksRV;
    private EditText subTaskEDT;
    private RelativeLayout edtRL;
    private ImageView backBT, micBT;
    private Button addSubTaskBT;
    private Intent intent;
    private String parentTaskId;
    private SubTaskAdapter subTaskAdapter;
    private List<SubTask> subTasks;
    private DatabaseReference mDatabase;
    private TextView setDueDateTV;
    private String dueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_tasks);

        intent = getIntent();
        parentTaskId = intent.getStringExtra("parentTaskId");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setDueDateTV = findViewById(R.id.setDueDateTV);
        addSubTaskBT = findViewById(R.id.addSubTaskBT);
        backBT = findViewById(R.id.backFromSubtasksBT);
        subTasksRV = findViewById(R.id.subTasksRV);
        edtRL = findViewById(R.id.edtRL);
        fab = findViewById(R.id.fab);
        subTaskEDT = findViewById(R.id.subTaskEDT);
        subTaskCL = findViewById(R.id.subTasksCL);
        micBT = findViewById(R.id.micIV);

        subTasks = new ArrayList<>();

        subTasksRV.setLayoutManager(new LinearLayoutManager(this));

        subTaskAdapter = new SubTaskAdapter(SubTasksActivity.this, parentTaskId, subTasks);
        subTasksRV.setAdapter(subTaskAdapter);

        readSubTasks();

        micBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechToText(view);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(SubTasksActivity.this, R.anim.fade_in);
                edtRL.startAnimation(animation);
                edtRL.setVisibility(View.VISIBLE);
            }
        });

        backBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addSubTaskBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    addSubTask();
                    edtRL.setVisibility(View.INVISIBLE);
                }
            }
        });

        setDueDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDueDate();
            }
        });

        subTaskCL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtRL.setVisibility(View.INVISIBLE);
            }
        });
    }
    private boolean validate() {
        if (subTaskEDT.getText().toString().isEmpty()) {
            subTaskEDT.setError("Wpisz nazwę");
            return false;
        }
        if(dueDate == null || dueDate.isEmpty()){
            setDueDateTV.setError("Wybierz termin");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDueDateTV.setError(null); // Usunięcie błędu po 2 sekundach
                }
            }, 2000);
            return false;
        }
        return true;
    }
    private void addSubTask() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("subtasks");
        SubTask subTask = new SubTask(
                ref.push().getKey(),
                subTaskEDT.getText().toString(),
                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                parentTaskId,
                dueDate);

        ref.child(subTask.getTaskId()).setValue(subTask);
    }
    private void readSubTasks() {
        FirebaseDatabase.getInstance().getReference().child("subtasks").orderByChild("parentTaskId").equalTo(parentTaskId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subTasks.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    SubTask subTask = snapshot1.getValue(SubTask.class);
                    subTasks.add(subTask);
                }
                sortSubTasks();
                subTaskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void sortSubTasks(){
        Collections.sort(subTasks, new Comparator<SubTask>() {
            @Override
            public int compare(SubTask subTask, SubTask t1) {
                return subTask.getDueDate().compareTo(t1.getDueDate());
            }
        });
    }

    private void setDueDate(){
        Calendar calendar = Calendar.getInstance();

        int MONTH = calendar.get(Calendar.MONTH);
        int YEAR = calendar.get(Calendar.YEAR);
        int DAY = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SubTasksActivity.this, new DatePickerDialog.OnDateSetListener() {
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
            subTaskEDT.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }
}