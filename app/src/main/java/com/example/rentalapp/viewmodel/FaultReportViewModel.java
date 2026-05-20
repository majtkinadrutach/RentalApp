package com.example.rentalapp.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rentalapp.model.FaultReport;
import com.example.rentalapp.repository.FaultReportRepository;

public class FaultReportViewModel extends AndroidViewModel {

    private final FaultReportRepository repository;

    private final MutableLiveData<String>  actionSuccess = new MutableLiveData<>();
    private final MutableLiveData<String>  errorMessage  = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading     = new MutableLiveData<>(false);

    public FaultReportViewModel(@NonNull Application application) {
        super(application);
        repository = new FaultReportRepository(application);
    }

    public LiveData<String>  getActionSuccess() { return actionSuccess; }
    public LiveData<String>  getErrorMessage()  { return errorMessage; }
    public LiveData<Boolean> getIsLoading()     { return isLoading; }

    public void submit(int assetId, String description, @Nullable Uri photoUri) {
        if (description.trim().isEmpty()) {
            errorMessage.setValue("Wpisz opis usterki przed wysłaniem.");
            return;
        }
        isLoading.setValue(true);
        repository.submit(assetId, description, photoUri,
                new FaultReportRepository.FaultCallback<FaultReport>() {
                    @Override public void onSuccess(FaultReport data) {
                        isLoading.setValue(false);
                        actionSuccess.setValue("Zgłoszenie wysłane!");
                    }
                    @Override public void onError(int code, String message) {
                        isLoading.setValue(false);
                        errorMessage.setValue(message);
                    }
                });
    }
}
