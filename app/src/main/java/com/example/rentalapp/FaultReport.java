package com.example.rentalapp;

import com.google.gson.annotations.SerializedName;

public class FaultReport {
    private int id;
    @SerializedName("user_id")
    private int userId;
    @SerializedName("asset_id")
    private int assetId;
    private String description;
    @SerializedName("photo_path")
    private String photoPath;
    @SerializedName("photo_url")
    private String photoUrl;
    private String status;
    @SerializedName("created_at")
    private String createdAt;
    private Asset asset;
    private User user;

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getAssetId() { return assetId; }
    public String getDescription() { return description; }
    public String getPhotoPath() { return photoPath; }
    public String getPhotoUrl() { return photoUrl; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public Asset getAsset() { return asset; }
    public User getUser() { return user; }
    public boolean isOpen() { return "open".equals(status); }
}
