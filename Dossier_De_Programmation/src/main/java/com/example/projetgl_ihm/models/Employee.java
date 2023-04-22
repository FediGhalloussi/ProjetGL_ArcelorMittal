package com.example.projetgl_ihm.models;

public class Employee {
    private String username;
    private String password;
    private String grade;

    public Employee(String username, String password, String grade) {
        this.username = username;
        this.password = password;
        this.grade = grade;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getGrade() {
        return this.grade;
    }
}