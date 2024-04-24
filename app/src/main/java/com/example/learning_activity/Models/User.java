package com.example.learning_activity.Models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private String id;
    private String password;
    private String token;

    public User() {
    }

    public User(String email, String id, String password, String token) {
        this.email = email;
        this.id = id;
        this.password = password;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", getEmail());
        result.put("id", getId());
        result.put("password", getPassword());
        result.put("token", getToken());

        return result;
    }
}
