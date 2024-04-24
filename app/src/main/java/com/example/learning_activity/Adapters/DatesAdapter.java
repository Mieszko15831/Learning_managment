package com.example.learning_activity.Adapters;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning_activity.Activities.SubTasksActivity;
import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.ViewHolder> {

    private Context context;
    private List<SubTask> subTasks;
    int day;
    String monthName = "";
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public DatesAdapter(Context context, List<SubTask> subTasks) {
        this.context = context;
        this.subTasks = subTasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.date_item_layout, parent, false);
        return new DatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubTask subTask = subTasks.get(position);
        try{
            Date date = format.parse(subTask.getDueDate());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            day = calendar.get(Calendar.DAY_OF_MONTH);
            monthName = new SimpleDateFormat("MMMM", Locale.getDefault()).format(date);
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }

        holder.subTaskNameTV.setText("Podzadanie: " + subTask.getName());
        if (subTask.getStatus().equals("0")){
            holder.subTaskStatusTV.setText("Status: Niewykonane");
        }
        else holder.subTaskStatusTV.setText("Status: Wykonane");

        holder.dayOfMonthTV.setText(Integer.toString(day));
        holder.monthTV.setText(monthName);
    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView dayOfMonthTV,
                monthTV,
                subTaskNameTV,
                subTaskStatusTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayOfMonthTV = itemView.findViewById(R.id.dayOfMonthTV);
            monthTV = itemView.findViewById(R.id.monthTV);
            subTaskNameTV = itemView.findViewById(R.id.subtaskNameTV);
            subTaskStatusTV = itemView.findViewById(R.id.subTaskStatusTV);
        }
    }
}
