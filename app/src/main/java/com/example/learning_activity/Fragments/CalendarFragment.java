package com.example.learning_activity.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;


import com.example.learning_activity.Adapters.DatesAdapter;
import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;
import com.google.android.material.datepicker.DayViewDecorator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment{

    private DatesAdapter datesAdapter;
    private RecyclerView subTasksRV;
    private List<SubTask> subTaskList;
    private List<Date> subTasksDates;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        subTaskList = new ArrayList<>();
        datesAdapter = new DatesAdapter(getContext(), subTaskList);
        subTasksRV = view.findViewById(R.id.subtasksRV);
        subTasksRV.setLayoutManager(new LinearLayoutManager(getContext()));
        subTasksRV.setAdapter(datesAdapter);
        readSubTasks();

        return view;
    }

    public void sortSubTasks() {
        Collections.sort(subTaskList, new Comparator<SubTask>() {
            @Override
            public int compare(SubTask subTask1, SubTask subTask2) {
                LocalDate date1 = LocalDate.parse(subTask1.getDueDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate date2 = LocalDate.parse(subTask2.getDueDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return date1.compareTo(date2);
            }
        });
    }
    private void readSubTasks(){

        FirebaseDatabase.getInstance().getReference().child("subtasks").orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subTaskList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    SubTask subTask = snapshot1.getValue(SubTask.class);
                    System.out.println(snapshot1.getValue().toString());
                    subTaskList.add(subTask);
                    //try{
                       //Date date = dateFormat.parse(subTask.getDueDate());
                       //subTasksDates.add(date);
                    //}
                    //catch (ParseException e) {
                   //  e.printStackTrace();
                    //}
                }
                sortSubTasks();
                datesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}