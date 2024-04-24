package com.example.learning_activity.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.learning_activity.Adapters.TaskAdapter;
import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;
import com.example.learning_activity.Models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TasksFragment extends Fragment {

    private TaskAdapter taskAdapter;
    private RecyclerView tasksGV;
    private List<Task> taskList;
    private List<SubTask> subTasks;

    public TasksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        tasksGV = view.findViewById(R.id.tasksGV);

        //tasksGV.setHasFixedSize(true);
        tasksGV.setLayoutManager(new GridLayoutManager(getContext(), 2));
        taskList = new ArrayList<>();
        subTasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(getContext(), taskList);
        tasksGV.setAdapter(taskAdapter);
        readTasks();
        return view;
    }

    private void readTasks(){
        FirebaseDatabase.getInstance().getReference().child("tasks").orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Task task = snapshot1.getValue(Task.class);
                    taskList.add(task);
                }
                taskAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}