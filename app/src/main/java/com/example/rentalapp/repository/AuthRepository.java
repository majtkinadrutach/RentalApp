package com.example.rentalapp.repository;

import android.content.Context;

import com.example.rentalapp.model.LoginRequest;
import com.example.rentalapp.model.LoginResponse;
import com.example.rentalapp.network.ApiClient;
import com.example.rentalapp.network.ApiService;
import com.example.rentalapp.util.ResponseHandler;
import com.example.rentalapp.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    public interface AuthCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    private final ApiService   api;
    private final TokenManager tokenManager;

    public AuthRepository(Context context) {
        this.api          = ApiClient.getService(context);
        this.tokenManager = TokenManager.getInstance(context);
    }

    public void login(String employeeId, String password, AuthCallback<LoginResponse> callback) {
        api.login(new LoginRequest(employeeId, password))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse body = response.body();
                            tokenManager.saveToken(body.getToken());
                            if (body.getUser() != null) {
                                tokenManager.saveUserInfo(
                                        body.getUser().getId(),
                                        body.getUser().getRole(),
                                        body.getUser().getName()
                                );
                            }
                            callback.onSuccess(body);
                        } else if (response.code() == 401) {
                            callback.onError("Nieprawidłowy identyfikator lub hasło.");
                        } else {
                            callback.onError(ResponseHandler.getMessage(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        callback.onError(ResponseHandler.getMessage(-1));
                    }
                });
    }

    public void logout(AuthCallback<Void> callback) {
        api.logout().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tokenManager.clear();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tokenManager.clear(); // Wyczyść lokalnie nawet jeśli serwer nie odpowie
                callback.onSuccess(null);
            }
        });
    }
}
