package com.example.rentalapp;

import com.google.gson.annotations.SerializedName;

public class User {
    private int id;
    private String name;
    private String email;
    @SerializedName("employee_id")
    private String employeeId;
    private String role;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getEmployeeId() { return employeeId; }
    public String getRole() { return role; }
    public boolean isAdmin() { return "admin".equals(role); }
}
