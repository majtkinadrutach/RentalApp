package com.example.rentalapp.network;

import android.content.Context;

import com.example.rentalapp.util.TokenManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // ╔══════════════════════════════════════════════════════════╗
    // ║  Zmień na docelowy URL po wdrożeniu backendu Laravel     ║
    // ║  Przykład: "https://twoja-domena.hostinger.com/api/"     ║
    // ║  Lokalny emulator Android: "http://10.0.2.2:8000/api/"  ║
    // ╚══════════════════════════════════════════════════════════╝
    public static final String BASE_URL = "https://api.dkaminski.xyz/api/";

    private static Retrofit retrofit;

    public static synchronized Retrofit getInstance(Context context) {
        if (retrofit == null) {
            TokenManager tokenManager = TokenManager.getInstance(context);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // W wersji release zmień na Level.NONE
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(tokenManager))
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getService(Context context) {
        return getInstance(context).create(ApiService.class);
    }
}
