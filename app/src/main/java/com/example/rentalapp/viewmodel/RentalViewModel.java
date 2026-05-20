package com.example.rentalapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rentalapp.model.Rental;
import com.example.rentalapp.repository.RentalRepository;

import java.util.List;

public class RentalViewModel extends AndroidViewModel {

    private final RentalRepository repository;

    private final MutableLiveData<List<Rental>> rentals        = new MutableLiveData<>();
    private final MutableLiveData<String>       actionSuccess  = new MutableLiveData<>();
    private final MutableLiveData<String>       errorMessage   = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      isLoading      = new MutableLiveData<>(false);

    public RentalViewModel(@NonNull Application application) {
        super(application);
        repository = new RentalRepository(application);
    }

    public LiveData<List<Rental>> getRentals()      { return rentals; }
    public LiveData<String>       getActionSuccess() { return actionSuccess; }
    public LiveData<String>       getErrorMessage()  { return errorMessage; }
    public LiveData<Boolean>      getIsLoading()     { return isLoading; }

    public void loadRentals() {
        isLoading.setValue(true);
        repository.getRentals(new RentalRepository.RentalCallback<List<Rental>>() {
            @Override public void onSuccess(List<Rental> data) {
                isLoading.setValue(false);
                rentals.setValue(data);
            }
            @Override public void onError(int code, String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void loadOverdueRentals() {
        isLoading.setValue(true);
        repository.getOverdueRentals(new RentalRepository.RentalCallback<List<Rental>>() {
            @Override public void onSuccess(List<Rental> data) {
                isLoading.setValue(false);
                rentals.setValue(data);
            }
            @Override public void onError(int code, String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void createRental(int assetId, String dueDate) {
        isLoading.setValue(true);
        repository.createRental(assetId, dueDate, new RentalRepository.RentalCallback<Rental>() {
            @Override public void onSuccess(Rental data) {
                isLoading.setValue(false);
                actionSuccess.setValue("Wypożyczono pomyślnie!");
            }
            @Override public void onError(int code, String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void returnByAssetId(int assetId) {
        isLoading.setValue(true);
        repository.returnByAssetId(assetId, new RentalRepository.RentalCallback<Rental>() {
            @Override public void onSuccess(Rental data) {
                isLoading.setValue(false);
                actionSuccess.setValue("Zwrócono pomyślnie!");
            }
            @Override public void onError(int code, String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    /**
     * Bulk wypożyczenie — kolejne wywołania dla każdego asset ID.
     * Po wszystkich odpowiedziach ustawia actionSuccess z podsumowaniem.
     */
    public void bulkRent(List<Integer> assetIds, String dueDate) {
        if (assetIds.isEmpty()) return;
        isLoading.setValue(true);

        int[] successCount = {0};
        int[] doneCount    = {0};
        int   total        = assetIds.size();

        for (int assetId : assetIds) {
            repository.createRental(assetId, dueDate, new RentalRepository.RentalCallback<Rental>() {
                @Override public void onSuccess(Rental data) {
                    successCount[0]++;
                    checkBulkDone(++doneCount[0], total, successCount[0]);
                }
                @Override public void onError(int code, String message) {
                    checkBulkDone(++doneCount[0], total, successCount[0]);
                }
            });
        }
    }

    /** Bulk zwrot — kolejne wywołania dla każdego asset ID. */
    public void bulkReturn(List<Integer> assetIds) {
        if (assetIds.isEmpty()) return;
        isLoading.setValue(true);

        int[] successCount = {0};
        int[] doneCount    = {0};
        int   total        = assetIds.size();

        for (int assetId : assetIds) {
            repository.returnByAssetId(assetId, new RentalRepository.RentalCallback<Rental>() {
                @Override public void onSuccess(Rental data) {
                    successCount[0]++;
                    checkBulkDone(++doneCount[0], total, successCount[0]);
                }
                @Override public void onError(int code, String message) {
                    checkBulkDone(++doneCount[0], total, successCount[0]);
                }
            });
        }
    }

    private void checkBulkDone(int done, int total, int successes) {
        if (done == total) {
            isLoading.postValue(false);
            if (successes == total) {
                actionSuccess.postValue("Zatwierdzono " + successes + " z " + total + " sztuk.");
            } else {
                actionSuccess.postValue("Zatwierdzono " + successes + " z " + total
                        + ". Część operacji nie powiodła się.");
            }
        }
    }
}
