package com.example.rentalapp.util;

/**
 * Centralne tłumaczenie kodów HTTP na komunikaty po polsku.
 * Używany we wszystkich repozytoriach zamiast inline stringów.
 */
public class ResponseHandler {

    private ResponseHandler() {}

    public static String getMessage(int code) {
        switch (code) {
            case 400: return "Nieprawidłowe żądanie.";
            case 401: return "Sesja wygasła. Zaloguj się ponownie.";
            case 403: return "Brak uprawnień do tej operacji.";
            case 404: return "Nie znaleziono zasobu.";
            case 409: return "Konflikt — sprawdź status sprzętu.";
            case 422: return "Nieprawidłowe dane. Sprawdź formularz.";
            case 500:
            case 502:
            case 503: return "Błąd serwera. Spróbuj ponownie za chwilę.";
            case -1:  return "Brak połączenia z serwerem.";
            default:  return "Nieznany błąd (" + code + ").";
        }
    }
}
