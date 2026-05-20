package com.example.rentalapp.network;

import com.example.rentalapp.model.Asset;
import com.example.rentalapp.model.FaultReport;
import com.example.rentalapp.model.LoginRequest;
import com.example.rentalapp.model.LoginResponse;
import com.example.rentalapp.model.Rental;
import com.example.rentalapp.model.RentalRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ════════════════════════════════════════
    //  Auth
    // ════════════════════════════════════════

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/logout")
    Call<Void> logout();

    // ════════════════════════════════════════
    //  Assets
    // ════════════════════════════════════════

    /** Wszystkie sprzęty (admin) */
    @GET("assets")
    Call<List<Asset>> getAssets();

    /** Filtrowanie po statusie: available | rented | damaged */
    @GET("assets")
    Call<List<Asset>> getAssetsByStatus(@Query("status") String status);

    /** Szczegóły po ID numerycznym */
    @GET("assets/{id}")
    Call<Asset> getAsset(@Path("id") int id);

    /** Szczegóły po numerze seryjnym (dla skanera QR) */
    @GET("assets/serial/{serial}")
    Call<Asset> getAssetBySerial(@Path("serial") String serial);

    // ════════════════════════════════════════
    //  Rentals
    // ════════════════════════════════════════

    /** Admin → wszystkie; User → własne */
    @GET("rentals")
    Call<List<Rental>> getRentals();

    /** Wypożycz sprzęt */
    @POST("rentals")
    Call<Rental> createRental(@Body RentalRequest request);

    /** Zwróć sprzęt */
    @PUT("rentals/{id}/return")
    Call<Rental> returnRental(@Path("id") int id);

    /** Lista przeterminowanych wypożyczeń (tylko admin) */
    @GET("rentals/overdue")
    Call<List<Rental>> getOverdueRentals();

    // ════════════════════════════════════════
    //  Fault Reports
    // ════════════════════════════════════════

    /** Admin → wszystkie; User → własne */
    @GET("fault-reports")
    Call<List<FaultReport>> getFaultReports();

    /**
     * Zgłoś usterkę z opcjonalnym zdjęciem (multipart).
     * Jeśli nie ma zdjęcia, przekaż photo = null.
     */
    @Multipart
    @POST("fault-reports")
    Call<FaultReport> submitFaultReport(
            @Part("asset_id")    RequestBody assetId,
            @Part("description") RequestBody description,
            @Part               MultipartBody.Part photo
    );

    /** Oznacz usterkę jako rozwiązaną (tylko admin) */
    @PUT("fault-reports/{id}/resolve")
    Call<FaultReport> resolveFaultReport(@Path("id") int id);
}
