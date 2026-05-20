package com.example.rentalapp.repository;

import android.content.Context;

import com.example.rentalapp.model.Asset;
import com.example.rentalapp.network.ApiClient;
import com.example.rentalapp.network.ApiService;
import com.example.rentalapp.util.ResponseHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetRepository {

    public interface AssetCallback<T> {
        void onSuccess(T data);
        void onError(int code, String message);
    }

    private final ApiService api;

    public AssetRepository(Context context) {
        this.api = ApiClient.getService(context);
    }

    public void loadAssets(String listType, AssetCallback<List<Asset>> callback) {
        Call<List<Asset>> call;
        switch (listType) {
            case "AVAILABLE":              call = api.getAssetsByStatus("available"); break;
            case "RENTED": case "RENTED_ALL": call = api.getAssetsByStatus("rented");    break;
            case "DAMAGED":                call = api.getAssetsByStatus("damaged");  break;
            default:                       call = api.getAssets();
        }

        call.enqueue(new Callback<List<Asset>>() {
            @Override
            public void onResponse(Call<List<Asset>> c, Response<List<Asset>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), ResponseHandler.getMessage(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Asset>> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }

    public void loadAssetBySerial(String serial, AssetCallback<Asset> callback) {
        String clean = serial.replace("#", "").trim();
        api.getAssetBySerial(clean).enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> c, Response<Asset> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else if (response.code() == 404) {
                    callback.onError(404, "Sprzęt \"" + clean + "\" nie istnieje w systemie.");
                } else {
                    callback.onError(response.code(), ResponseHandler.getMessage(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Asset> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }
}
