package com.example.rentalapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rentalapp.model.Asset;
import com.example.rentalapp.model.Rental;
import com.example.rentalapp.repository.AssetRepository;
import com.example.rentalapp.repository.RentalRepository;

import java.util.List;

public class AdminReportViewModel extends AndroidViewModel {

    private final AssetRepository  assetRepository;
    private final RentalRepository rentalRepository;

    private final MutableLiveData<Integer>      rentedCount   = new MutableLiveData<>();
    private final MutableLiveData<Integer>      damagedCount  = new MutableLiveData<>();
    private final MutableLiveData<List<Rental>> overdueList   = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      isLoading     = new MutableLiveData<>(false);
    private final MutableLiveData<String>       errorMessage  = new MutableLiveData<>();

    // Licznik ukończonych zapytań (łącznie 3)
    private int completedCalls = 0;

    public AdminReportViewModel(@NonNull Application application) {
        super(application);
        assetRepository  = new AssetRepository(application);
        rentalRepository = new RentalRepository(application);
    }

    public LiveData<Integer>      getRentedCount()  { return rentedCount; }
    public LiveData<Integer>      getDamagedCount() { return damagedCount; }
    public LiveData<List<Rental>> getOverdueList()  { return overdueList; }
    public LiveData<Boolean>      getIsLoading()    { return isLoading; }
    public LiveData<String>       getErrorMessage() { return errorMessage; }

    /** Ładuje wszystkie trzy dane równolegle */
    public void loadReport() {
        completedCalls = 0;
        isLoading.setValue(true);

        // 1. Liczba wypożyczonych
        assetRepository.loadAssets("RENTED", new AssetRepository.AssetCallback<List<Asset>>() {
            @Override public void onSuccess(List<Asset> data) {
                rentedCount.postValue(data.size());
                checkAllDone();
            }
            @Override public void onError(int code, String message) {
                rentedCount.postValue(0);
                checkAllDone();
            }
        });

        // 2. Liczba uszkodzonych
        assetRepository.loadAssets("DAMAGED", new AssetRepository.AssetCallback<List<Asset>>() {
            @Override public void onSuccess(List<Asset> data) {
                damagedCount.postValue(data.size());
                checkAllDone();
            }
            @Override public void onError(int code, String message) {
                damagedCount.postValue(0);
                checkAllDone();
            }
        });

        // 3. Lista przeterminowanych
        rentalRepository.getOverdueRentals(new RentalRepository.RentalCallback<List<Rental>>() {
            @Override public void onSuccess(List<Rental> data) {
                overdueList.postValue(data);
                checkAllDone();
            }
            @Override public void onError(int code, String message) {
                errorMessage.postValue(message);
                checkAllDone();
            }
        });
    }

    private synchronized void checkAllDone() {
        completedCalls++;
        if (completedCalls >= 3) {
            isLoading.postValue(false);
        }
    }
}
