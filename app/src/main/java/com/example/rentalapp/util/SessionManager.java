package com.example.rentalapp.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Singleton obserwowany przez MainActivity.
 * AuthInterceptor wywołuje notifySessionExpired() na wątku sieciowym (postValue),
 * MainActivity obserwuje i robi logout + nawigację na głównym wątku.
 */
public class SessionManager {

    private static SessionManager instance;

    private final MutableLiveData<Boolean> sessionExpired = new MutableLiveData<>(false);

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public LiveData<Boolean> getSessionExpired() { return sessionExpired; }

    /** Wywoływane z wątku sieciowego — postValue zamiast setValue */
    public void notifySessionExpired() {
        sessionExpired.postValue(true);
    }

    /** Resetuj po obsłużeniu — żeby observer nie odpala się ponownie */
    public void reset() {
        sessionExpired.postValue(false);
    }
}
