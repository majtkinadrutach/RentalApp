package com.example.rentalapp.repository;

import android.content.Context;

import com.example.rentalapp.model.Rental;
import com.example.rentalapp.model.RentalRequest;
import com.example.rentalapp.network.ApiClient;
import com.example.rentalapp.network.ApiService;
import com.example.rentalapp.util.ResponseHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentalRepository {

    public interface RentalCallback<T> {
        void onSuccess(T data);
        void onError(int code, String message);
    }

    private final ApiService api;

    public RentalRepository(Context context) {
        this.api = ApiClient.getService(context);
    }

    // ── Pobierz listę wypożyczeń ───────────────────────────────────────────────
    public void getRentals(RentalCallback<List<Rental>> callback) {
        api.getRentals().enqueue(new Callback<List<Rental>>() {
            @Override
            public void onResponse(Call<List<Rental>> c, Response<List<Rental>> r) {
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(r.body());
                else callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
            }
            @Override
            public void onFailure(Call<List<Rental>> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }

    // ── Pobierz przeterminowane (admin) ───────────────────────────────────────
    public void getOverdueRentals(RentalCallback<List<Rental>> callback) {
        api.getOverdueRentals().enqueue(new Callback<List<Rental>>() {
            @Override
            public void onResponse(Call<List<Rental>> c, Response<List<Rental>> r) {
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(r.body());
                else callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
            }
            @Override
            public void onFailure(Call<List<Rental>> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }

    // ── Utwórz wypożyczenie ───────────────────────────────────────────────────
    public void createRental(int assetId, String dueDate, RentalCallback<Rental> callback) {
        api.createRental(new RentalRequest(assetId, dueDate))
                .enqueue(new Callback<Rental>() {
                    @Override
                    public void onResponse(Call<Rental> c, Response<Rental> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            callback.onSuccess(r.body());
                        } else if (r.code() == 409) {
                            callback.onError(409, "Sprzęt nie jest już dostępny.");
                        } else {
                            callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
                        }
                    }
                    @Override
                    public void onFailure(Call<Rental> c, Throwable t) {
                        callback.onError(-1, ResponseHandler.getMessage(-1));
                    }
                });
    }

    // ── Zwróć po ID wypożyczenia ──────────────────────────────────────────────
    public void returnRental(int rentalId, RentalCallback<Rental> callback) {
        api.returnRental(rentalId).enqueue(new Callback<Rental>() {
            @Override
            public void onResponse(Call<Rental> c, Response<Rental> r) {
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(r.body());
                else callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
            }
            @Override
            public void onFailure(Call<Rental> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }

    /**
     * Zwróć sprzęt znając tylko jego asset ID.
     * Krok 1: pobierz listę wypożyczeń usera.
     * Krok 2: znajdź aktywne wypożyczenie dla danego asset_id.
     * Krok 3: wywołaj returnRental(rentalId).
     */
    public void returnByAssetId(int assetId, RentalCallback<Rental> callback) {
        api.getRentals().enqueue(new Callback<List<Rental>>() {
            @Override
            public void onResponse(Call<List<Rental>> c, Response<List<Rental>> r) {
                if (!r.isSuccessful() || r.body() == null) {
                    callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
                    return;
                }
                Rental active = null;
                for (Rental rental : r.body()) {
                    if (rental.getAssetId() == assetId && !rental.isReturned()) {
                        active = rental;
                        break;
                    }
                }
                if (active == null) {
                    callback.onError(404, "Nie znaleziono aktywnego wypożyczenia dla tego sprzętu.");
                    return;
                }
                returnRental(active.getId(), callback);
            }
            @Override
            public void onFailure(Call<List<Rental>> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }
}
