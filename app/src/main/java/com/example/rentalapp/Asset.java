package com.example.rentalapp;

import com.google.gson.annotations.SerializedName;

public class Asset {
    private int id;
    private String name;
    private String category;
    private String status;
    @SerializedName("serial_number")
    private String serialNumber;
    private String description;
    @SerializedName("active_rental")
    private Rental activeRental;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getSerialNumber() { return serialNumber; }
    public String getDescription() { return description; }
    public Rental getActiveRental() { return activeRental; }

    public String getDisplayId() {
        return serialNumber != null ? serialNumber : "#" + id;
    }

    public String getDisplayStatus() {
        if (status == null) return "Nieznany";
        switch (status) {
            case "available": return "Dostępny";
            case "rented":    return "Wypożyczony";
            case "damaged":   return "Uszkodzony";
            default:          return status;
        }
    }
}
