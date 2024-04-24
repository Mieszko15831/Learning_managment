package com.example.learning_activity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning_activity.Activities.SubTasksActivity;
import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.Models.Task;
import com.example.learning_activity.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context context;
    private List<Task> tasks;
    private int d = 0;
    private double percent = 0;
    private boolean isEditing = false;
    private DatabaseReference mDatabase;
    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.task_item_layout,parent, false);
            return new TaskAdapter.ViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        readSubTasks(task, holder);
        String taskId = task.getTaskId();
        holder.taskNameTV.setText(task.getName());
        holder.completeStatusTV.setText("Ukończono " + percent + "%");

        holder.taskNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubTasksActivity.class);
                intent.putExtra("parentTaskId", taskId);
                context.startActivity(intent);
            }
        });

        holder.taskNameTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEditing) {
                    isEditing = true;
                    holder.taskNameTV.setVisibility(View.GONE);
                    holder.taskNameEDT.setVisibility(View.VISIBLE);
                    holder.taskNameEDT.setText(holder.taskNameTV.getText().toString());
                    holder.taskNameEDT.requestFocus();
                    holder.iconsLL.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        holder.taskNameEDT.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    holder.taskNameEDT.setVisibility(View.GONE);
                    holder.taskNameTV.setVisibility(View.VISIBLE);
                    holder.iconsLL.setVisibility(View.GONE);
                    isEditing = false;
                    // Aktualizuj tekst w TextView na podstawie tekstu wprowadzonego w EditText
                    holder.taskNameTV.setText(holder.taskNameEDT.getText().toString());
                    updateTask(task, holder);
                }
            }
        });

        holder.doneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.iconsLL.setVisibility(View.GONE);
                holder.taskNameEDT.setVisibility(View.GONE);
                holder.taskNameTV.setVisibility(View.VISIBLE);
                updateTask(task, holder);
            }
        });

        holder.deleteBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTask(task);
                notifyDataSetChanged();
            }
        });

    }


    private void updateTask(Task task, ViewHolder holder){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("tasks").child(task.getTaskId());

        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put("name", holder.taskNameEDT.getText().toString());

        mDatabase.updateChildren(updateValues);
    }

    private void deleteTask(Task task){
        FirebaseDatabase.getInstance().getReference().child("subtasks").orderByChild("parentTaskId").equalTo(task.getTaskId()).addValueEventListener(new ValueEventListener() {
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

        FirebaseDatabase.getInstance().getReference().child("tasks").child(task.getTaskId()).addValueEventListener(new ValueEventListener() {
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

    }

    private void readSubTasks(Task task, ViewHolder holder) {
        FirebaseDatabase.getInstance().getReference().child("subtasks").orderByChild("parentTaskId").equalTo(task.getTaskId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int completedSubTasks = 0;
                int totalSubTasks = (int) snapshot.getChildrenCount();

                if(totalSubTasks > 0){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        SubTask subTask = snapshot1.getValue(SubTask.class);
                        if(subTask.getStatus().equals("1")){
                            completedSubTasks++;
                        }
                    }
                    double completePercentage = (double) completedSubTasks / totalSubTasks * 100;
                    double roundedPercentage = (double) Math.round(completePercentage * 10) / 10;
                    holder.completeStatusTV.setText("Ukonczono: " + roundedPercentage + "%");
                }
                else{
                    holder.completeStatusTV.setText("Ukonczono: " + 0 + "%");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView taskNameTV;
        private TextView completeStatusTV;
        private EditText taskNameEDT;
        private ImageView doneBT;
        private ImageView deleteBT;
        private LinearLayout iconsLL;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            taskNameTV = itemView.findViewById(R.id.taskNameTV);
            completeStatusTV = itemView.findViewById(R.id.completeStatusTV);
            taskNameEDT = itemView.findViewById(R.id.taskNameEDT);
            doneBT = itemView.findViewById(R.id.doneBT);
            deleteBT = itemView.findViewById(R.id.deleteBT);
            iconsLL = itemView.findViewById(R.id.iconsLL);
        }
    }
}
