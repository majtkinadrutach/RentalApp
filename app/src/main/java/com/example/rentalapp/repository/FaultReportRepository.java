package com.example.rentalapp.repository;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.example.rentalapp.model.FaultReport;
import com.example.rentalapp.network.ApiClient;
import com.example.rentalapp.network.ApiService;
import com.example.rentalapp.util.ResponseHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaultReportRepository {

    public interface FaultCallback<T> {
        void onSuccess(T data);
        void onError(int code, String message);
    }

    private final ApiService api;
    private final Context    context;

    public FaultReportRepository(Context context) {
        this.api     = ApiClient.getService(context);
        this.context = context.getApplicationContext();
    }

    // ── Pobierz listę zgłoszeń ────────────────────────────────────────────────
    public void getFaultReports(FaultCallback<List<FaultReport>> callback) {
        api.getFaultReports().enqueue(new Callback<List<FaultReport>>() {
            @Override
            public void onResponse(Call<List<FaultReport>> c, Response<List<FaultReport>> r) {
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(r.body());
                else callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
            }
            @Override
            public void onFailure(Call<List<FaultReport>> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }

    /**
     * Wyślij zgłoszenie usterki z opcjonalnym zdjęciem.
     *
     * @param assetId     numeryczne ID sprzętu z API
     * @param description opis usterki
     * @param photoUri    URI zdjęcia z FileProvider lub null jeśli brak
     */
    public void submit(int assetId, String description,
                       @Nullable Uri photoUri,
                       FaultCallback<FaultReport> callback) {

        RequestBody assetIdBody  = RequestBody.create(
                MediaType.parse("text/plain"), String.valueOf(assetId));
        RequestBody descBody     = RequestBody.create(
                MediaType.parse("text/plain"), description);

        // Konwertuj URI → File → MultipartBody.Part (lub null jeśli brak zdjęcia)
        MultipartBody.Part photoPart = null;
        if (photoUri != null) {
            File photoFile = uriToTempFile(photoUri);
            if (photoFile != null) {
                RequestBody photoBody = RequestBody.create(
                        MediaType.parse("image/jpeg"), photoFile);
                photoPart = MultipartBody.Part.createFormData(
                        "photo", photoFile.getName(), photoBody);
            }
        }

        api.submitFaultReport(assetIdBody, descBody, photoPart)
                .enqueue(new Callback<FaultReport>() {
                    @Override
                    public void onResponse(Call<FaultReport> c, Response<FaultReport> r) {
                        if (r.isSuccessful() && r.body() != null) callback.onSuccess(r.body());
                        else callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
                    }
                    @Override
                    public void onFailure(Call<FaultReport> c, Throwable t) {
                        callback.onError(-1, ResponseHandler.getMessage(-1));
                    }
                });
    }

    // ── Oznacz jako rozwiązane (admin) ────────────────────────────────────────
    public void resolve(int reportId, FaultCallback<FaultReport> callback) {
        api.resolveFaultReport(reportId).enqueue(new Callback<FaultReport>() {
            @Override
            public void onResponse(Call<FaultReport> c, Response<FaultReport> r) {
                if (r.isSuccessful() && r.body() != null) callback.onSuccess(r.body());
                else callback.onError(r.code(), ResponseHandler.getMessage(r.code()));
            }
            @Override
            public void onFailure(Call<FaultReport> c, Throwable t) {
                callback.onError(-1, ResponseHandler.getMessage(-1));
            }
        });
    }

    /**
     * Kopiuje zawartość URI do pliku tymczasowego w cache.
     * Działa z każdym typem URI (content://, file://).
     */
    @Nullable
    private File uriToTempFile(Uri uri) {
        try {
            InputStream in = context.getContentResolver().openInputStream(uri);
            if (in == null) return null;

            File temp = new File(context.getCacheDir(),
                    "fault_upload_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(temp);

            byte[] buf = new byte[4096];
            int    len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);

            out.close();
            in.close();
            return temp;
        } catch (IOException e) {
            return null;
        }
    }
}
