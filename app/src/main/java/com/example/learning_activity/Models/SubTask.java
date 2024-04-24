package com.example.learning_activity.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class SubTask extends Task {
    private String parentTaskId;
    private String dueDate;
    private String status;

    public SubTask() {

    }

    public SubTask(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public SubTask(String taskId, String name, String userId, String parentTaskId, String dueDate) {
        super(taskId, name, userId);
        this.parentTaskId = parentTaskId;
        this.dueDate = dueDate;
        this.status = "0";
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("taskId", getTaskId());
        result.put("name", getName());
        result.put("userId", getUserId());
        result.put("parentTaskId", parentTaskId);
        result.put("dueDate", dueDate);
        result.put("status", status);

        return result;
    }
}
