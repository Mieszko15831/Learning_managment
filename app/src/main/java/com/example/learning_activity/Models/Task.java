package com.example.learning_activity.Models;

import java.util.List;

public class Task {

    private String taskId;
    private String name;
    private String userId;

    public Task() {
    }

    public Task(String taskId, String name, String userId) {
        this.taskId = taskId;
        this.name = name;
        this.userId = userId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
