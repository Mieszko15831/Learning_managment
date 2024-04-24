package com.example.learning_activity.Fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatsFragment extends Fragment {
    private Handler handler;
    private BarChart barChart;
    private DatabaseReference mDatabase;
    int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    private List<SubTask> subTasks;
    private List<Date> dates;
    private float[] tasksByMonth;
    private float doneSubTasks;
    private int month;
    private int secondmonth;
    private List<String> monthNames = Arrays.asList(
            "","St", "Lut", "Marz",
            "Kw", "M", "Cz",
            "Li", "Si", "Wrz",
            "Paź", "List", "Gr");

    public StatsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        subTasks = new ArrayList<>();
        handler = new Handler();
        dates = new ArrayList<>();
        tasksByMonth = new float[12];
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("subtasks").orderByChild("userId").equalTo(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            SubTask subTask = snapshot1.getValue(SubTask.class);
                            subTasks.add(subTask);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        barChart = view.findViewById(R.id.bar_chart);

        //Inizjalizacja listy
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tasksByMonth = new float[12];
                doneSubTasks = 0f;

                for (int i = 0; i < subTasks.size(); i++) {
                    try {
                        Date date = format.parse(subTasks.get(i).getDueDate());
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        int month = calendar.get(Calendar.MONTH);

                        // Sprawdzenie czy podzadanie zostało wykonane
                        if (subTasks.get(i).getStatus().equals("1")) {
                            // Inkrementacja licznika wykonanych podzadań dla danego miesiąca
                            tasksByMonth[month] += 1f;
                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                for(int i = 1; i < monthNames.size(); i++){
                    float value = (float) i;

                    BarEntry barEntry = new BarEntry(i, tasksByMonth[i - 1]);

                    barEntries.add(barEntry);
                }

                BarDataSet barDataSet = new BarDataSet(barEntries, "Ilośc wykonanych zadań");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setDrawValues(false);
                barChart.setData(new BarData(barDataSet));
                barChart.animateY(1000);
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(monthNames));
            }
        }, 4000);

        return view;
    }
}