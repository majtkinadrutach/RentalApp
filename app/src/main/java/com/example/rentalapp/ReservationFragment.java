package com.example.rentalapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Calendar;

public class ReservationFragment extends Fragment {

    private TextView textDateFrom;
    private TextView textDateTo;
    private Spinner  spinnerCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        textDateFrom    = view.findViewById(R.id.textDateFrom);
        textDateTo      = view.findViewById(R.id.textDateTo);
        EditText inputQuantity = view.findViewById(R.id.inputQuantity);
        Button   buttonConfirm = view.findViewById(R.id.buttonConfirmReservation);

        String[] categories = {"Laptopy", "Myszki", "Klawiatury", "Tablety", "Zasilacze", "Kable"};
        spinnerCategory.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories));

        textDateFrom.setOnClickListener(v -> showDatePicker(textDateFrom, null));
        textDateTo.setOnClickListener(v -> {
            // Data "do" nie może być wcześniejsza niż "od"
            String rawFrom = textDateFrom.getText().toString();
            showDatePicker(textDateTo, rawFrom.contains("Wybierz") ? null : rawFrom);
        });

        buttonConfirm.setOnClickListener(v -> {
            String category = spinnerCategory.getSelectedItem().toString();
            String dateFrom = textDateFrom.getText().toString();
            String dateTo   = textDateTo.getText().toString();
            String quantityStr = inputQuantity.getText().toString().trim();

            // ── Walidacja ──────────────────────────────────────────────────────
            if (dateFrom.contains("Wybierz") || dateTo.contains("Wybierz")) {
                showError("Wybierz daty rezerwacji.");
                return;
            }
            if (quantityStr.isEmpty()) {
                showError("Podaj ilość sztuk.");
                return;
            }
            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                showError("Ilość musi być liczbą całkowitą.");
                return;
            }
            if (quantity <= 0) {
                showError("Ilość musi być większa niż 0.");
                return;
            }

            // ── Potwierdzenie ──────────────────────────────────────────────────
            String message = "Kategoria: " + category
                    + "\nIlość: " + quantity + " szt."
                    + "\nOd: " + dateFrom
                    + "\nDo: " + dateTo;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Potwierdzenie rezerwacji")
                    .setMessage(message)
                    .setPositiveButton("Zarezerwuj", (dialog, which) -> {
                        // TODO: wywołaj POST /api/reservations gdy backend będzie gotowy
                        // ReservationRequest req = new ReservationRequest(category, quantity, dateFrom, dateTo);
                        // reservationViewModel.createReservation(req);
                        NavController nav = Navigation.findNavController(view);
                        nav.popBackStack();
                    })
                    .setNegativeButton("Anuluj", null)
                    .setCancelable(false)
                    .show();
        });
    }

    private void showDatePicker(TextView target, @Nullable String minDateStr) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (dp, year, month, day) -> {
                    String formatted = day + "/" + (month + 1) + "/" + year;
                    target.setText(formatted);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));

        // Ustaw minimalną datę jeśli podana (dla pola "do")
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void showError(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Błąd")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
