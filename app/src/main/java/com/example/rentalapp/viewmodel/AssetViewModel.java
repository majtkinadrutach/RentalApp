package com.example.rentalapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rentalapp.model.Asset;
import com.example.rentalapp.repository.AssetRepository;

import java.util.List;

public class AssetViewModel extends AndroidViewModel {

    private final AssetRepository repository;

    private final MutableLiveData<List<Asset>> assets        = new MutableLiveData<>();
    private final MutableLiveData<Asset>       selectedAsset = new MutableLiveData<>();
    private final MutableLiveData<Boolean>     isLoading     = new MutableLiveData<>(false);
    private final MutableLiveData<String>      errorMessage  = new MutableLiveData<>();

    public AssetViewModel(@NonNull Application application) {
        super(application);
        repository = new AssetRepository(application);
    }

    public LiveData<List<Asset>> getAssets()        { return assets; }
    public LiveData<Asset>       getSelectedAsset() { return selectedAsset; }
    public LiveData<Boolean>     getIsLoading()     { return isLoading; }
    public LiveData<String>      getErrorMessage()  { return errorMessage; }

    /** Ładuje listę sprzętu. listType: "ALL" | "AVAILABLE" | "RENTED" | "RENTED_ALL" | "DAMAGED" */
    public void loadAssets(String listType) {
        isLoading.setValue(true);
        repository.loadAssets(listType, new AssetRepository.AssetCallback<List<Asset>>() {
            @Override
            public void onSuccess(List<Asset> data) {
                isLoading.setValue(false);
                assets.setValue(data);
            }

            @Override
            public void onError(int code, String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    /** Ładuje szczegóły sprzętu po numerze seryjnym (np. po skanowaniu QR) */
    public void loadAssetBySerial(String serial) {
        isLoading.setValue(true);
        repository.loadAssetBySerial(serial, new AssetRepository.AssetCallback<Asset>() {
            @Override
            public void onSuccess(Asset data) {
                isLoading.setValue(false);
                selectedAsset.setValue(data);
            }

            @Override
            public void onError(int code, String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }
}
