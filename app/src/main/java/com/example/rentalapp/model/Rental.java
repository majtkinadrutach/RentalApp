package com.example.rentalapp.model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Rental {

    @SerializedName("id")
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

    @SerializedName("asset")
    private Asset asset;

    @SerializedName("user")
    private User user;

    public int    getId()         { return id; }
    public int    getUserId()     { return userId; }
    public int    getAssetId()    { return assetId; }
    public String getRentedAt()   { return rentedAt; }
    public String getDueDate()    { return dueDate; }
    public String getReturnedAt() { return returnedAt; }
    public Asset  getAsset()      { return asset; }
    public User   getUser()       { return user; }

    public boolean isReturned() { return returnedAt != null; }

    /** Imię i nazwisko użytkownika lub fallback na ID jeśli brak eager load */
    public String getUserDisplayName() {
        if (user != null && user.getName() != null) return user.getName();
        return "Użytkownik #" + userId;
    }

    /** Nazwa sprzętu lub fallback na ID */
    public String getAssetDisplayName() {
        if (asset != null && asset.getName() != null) return asset.getName();
        return "Sprzęt #" + assetId;
    }

    /**
     * Liczba dni opóźnienia. Zwraca 0 jeśli jeszcze nie po terminie lub już zwrócone.
     */
    public int getOverdueDays() {
        if (dueDate == null || isReturned()) return 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date due = sdf.parse(dueDate);
            Date now = new Date();
            if (due == null || !now.after(due)) return 0;
            return (int) ((now.getTime() - due.getTime()) / (1000L * 60 * 60 * 24));
        } catch (ParseException e) {
            return 0;
        }
    }
}
