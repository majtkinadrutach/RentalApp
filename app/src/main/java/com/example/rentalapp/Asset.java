package com.example.rentalapp;

public class Asset {
    private String id;
    private String name;
    private String category;
    private String status;

    public Asset(String id, String name, String category, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
}