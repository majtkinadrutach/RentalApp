package com.example.rentalapp.model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("employee_id")
    private String employeeId;

    @SerializedName("role")
    private String role;

    public int    getId()         { return id; }
    public String getName()       { return name; }
    public String getEmployeeId() { return employeeId; }
    public String getRole()       { return role; }

    public boolean isAdmin() { return "admin".equals(role); }
}
