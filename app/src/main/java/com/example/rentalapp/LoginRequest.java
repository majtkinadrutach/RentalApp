package com.example.rentalapp;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("employee_id")
    private final String employeeId;
    private final String password;

    public LoginRequest(String employeeId, String password) {
        this.employeeId = employeeId;
        this.password = password;
    }
}
