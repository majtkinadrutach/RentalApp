package com.example.rentalapp.model;

import com.google.gson.annotations.SerializedName;

public class FaultReport {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("asset_id")
    private int assetId;

    @SerializedName("description")
    private String description;

    @SerializedName("photo_url")
    private String photoUrl;

    @SerializedName("status")
    private String status; // "open" | "resolved"

    @SerializedName("asset")
    private Asset asset;

    public int    getId()          { return id; }
    public int    getUserId()      { return userId; }
    public int    getAssetId()     { return assetId; }
    public String getDescription() { return description; }
    public String getPhotoUrl()    { return photoUrl; }
    public String getStatus()      { return status; }
    public Asset  getAsset()       { return asset; }

    public boolean isOpen() { return "open".equals(status); }
}
