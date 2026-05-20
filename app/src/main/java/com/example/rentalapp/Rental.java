package com.example.rentalapp;

import com.google.gson.annotations.SerializedName;

public class Rental {
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("asset_id")
    private int assetId;
    @SerializedName("rented_at")
    private String rentedAt;
    @SerializedName("due_date")
    private String dueDate;
    @SerializedName("returned_at")
    private String returnedAt;
    @SerializedName("days_overdue")
    private int daysOverdue;
    private Asset asset;
    private User user;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getAssetId() { return assetId; }
    public String getRentedAt() { return rentedAt; }
    public String getDueDate() { return dueDate; }
    public String getReturnedAt() { return returnedAt; }
    public int getDaysOverdue() { return daysOverdue; }
    public Asset getAsset() { return asset; }
    public User getUser() { return user; }
    public boolean isReturned() { return returnedAt != null; }
}
