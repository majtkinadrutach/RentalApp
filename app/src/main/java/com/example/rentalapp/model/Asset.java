package com.example.rentalapp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model sprzętu — działa zarówno z danymi z API (Gson), jak i z lokalnym konstruktorem
 * używanym przez istniejący kod podczas migracji.
 *
 * UWAGA: getId() zwraca String w formacie "#LT-001" lub "#42" —
 * kompatybilne z AssetAdapter bez żadnych zmian w adapterze.
 */
public class Asset {

    @SerializedName("id")
    private int numericId;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    /**
     * Status z API: "available" | "rented" | "damaged"
     * Lokalny mock używa polskich wartości — getStatus() tłumaczy oba.
     */
    @SerializedName("status")
    private String status;

    @SerializedName("serial_number")
    private String serialNumber;

    @SerializedName("description")
    private String description;

    // ── Konstruktor lokalny (kompatybilność wsteczna z mock danymi) ──────────
    public Asset(String serialNumber, String name, String category, String status) {
        this.serialNumber = serialNumber;
        this.name         = name;
        this.category     = category;
        this.status       = status;
    }

    // ── Gettery ──────────────────────────────────────────────────────────────

    public int getNumericId() { return numericId; }

    /** Zwraca ID w formacie wyświetlanym — "#LT-001" lub "#42" */
    public String getId() {
        if (serialNumber != null && !serialNumber.isEmpty()) {
            return "#" + serialNumber;
        }
        return "#" + numericId;
    }

    public String getName()         { return name; }
    public String getCategory()     { return category; }
    public String getSerialNumber() { return serialNumber; }
    public String getDescription()  { return description; }

    /** Zwraca status po polsku niezależnie od źródła (API po angielsku / lokalny po polsku) */
    public String getStatus() {
        if (status == null) return "Nieznany";
        switch (status) {
            case "available":   return "Dostępny";
            case "rented":      return "Wypożyczony";
            case "damaged":     return "Uszkodzony";
            // Wartości lokalne (mock) — przepuść bez zmian
            default:            return status;
        }
    }

    /** Oryginalny status z API (do logiki biznesowej, nie do wyświetlania) */
    public String getRawStatus() { return status; }
}
