package com.example.rentalapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.rentalapp.model.LoginResponse;
import com.example.rentalapp.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;

    private final MutableLiveData<LoginResponse> loginResult  = new MutableLiveData<>();
    private final MutableLiveData<String>        errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean>       isLoading    = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public LiveData<LoginResponse> getLoginResult()  { return loginResult; }
    public LiveData<String>        getErrorMessage() { return errorMessage; }
    public LiveData<Boolean>       getIsLoading()    { return isLoading; }

    public void login(String employeeId, String password) {
        if (employeeId.trim().isEmpty() || password.isEmpty()) {
            errorMessage.setValue("Wypełnij wszystkie pola.");
            return;
        }

        isLoading.setValue(true);
        repository.login(employeeId.trim(), password, new AuthRepository.AuthCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse data) {
                isLoading.setValue(false);
                loginResult.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void logout() {
        repository.logout(new AuthRepository.AuthCallback<Void>() {
            @Override public void onSuccess(Void data) {}
            @Override public void onError(String message) {}
        });
    }
}
