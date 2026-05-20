package com.example.rentalapp.network;

import com.example.rentalapp.util.SessionManager;
import com.example.rentalapp.util.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder()
                .header("Accept", "application/json");

        String token = tokenManager.getToken();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(builder.build());

        // ── Globalna obsługa 401 ──────────────────────────────────────────────
        // Interceptor działa na wątku sieciowym — SessionManager.postValue()
        // przekazuje zdarzenie do głównego wątku, gdzie MainActivity robi logout.
        if (response.code() == 401) {
            tokenManager.clear();
            SessionManager.getInstance().notifySessionExpired();
        }

        return response;
    }
}
