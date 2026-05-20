package com.example.rentalapp.model;

import com.google.gson.annotations.SerializedName;

public class RentalRequest {

    @SerializedName("asset_id")
    private final int assetId;

    @SerializedName("due_date")
    private final String dueDate; // format: "yyyy-MM-dd"

    public RentalRequest(int assetId, String dueDate) {
        this.assetId = assetId;
        this.dueDate = dueDate;
    }
}
