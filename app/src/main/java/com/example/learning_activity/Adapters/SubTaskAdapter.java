package com.example.learning_activity.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learning_activity.Activities.EditSubTaskActivity;
import com.example.learning_activity.Models.SubTask;
import com.example.learning_activity.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubTaskAdapter extends RecyclerView.Adapter<SubTaskAdapter.ViewHolder> {

    private Context context;
    private String parentTaskId;
    private List<SubTask> subTasks;
    private DatabaseReference mDatabase;

    public SubTaskAdapter(Context context, String parentTaskId, List<SubTask> subTasks) {
        this.context = context;
        this.parentTaskId = parentTaskId;
        this.subTasks = subTasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subtask_item_layout, parent, false);
        return new SubTaskAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubTask subTask = subTasks.get(position);

        holder.subTaskTV.setText(subTask.getName());
        holder.dueDateTV.setText(subTask.getDueDate());
        if(subTask.getStatus().equals("1")){
            holder.checkBox.setChecked(true);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditSubTaskActivity.class);
                intent.putExtra("subTaskName", subTask.getName());
                intent.putExtra("taskId", subTask.getTaskId());
                intent.putExtra("dueDate", subTask.getDueDate());
                context.startActivity(intent);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child("subtasks").child(subTask.getTaskId());
                Map<String, Object> updateValues = new HashMap<>();

                if(b){
                    subTask.setStatus("1");
                    updateValues.put("status", subTask.getStatus());

                } else if (!b) {
                    subTask.setStatus("0");
                    updateValues.put("status", subTask.getStatus());
                }
                mDatabase.updateChildren(updateValues);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return subTasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView subTaskTV;
        private TextView dueDateTV;
        private TextView completeStatusTV;
        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subTaskTV = itemView.findViewById(R.id.subtaskNameTV);
            dueDateTV = itemView.findViewById(R.id.dueDateTV);
            completeStatusTV = itemView.findViewById(R.id.completeStatusTV);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
