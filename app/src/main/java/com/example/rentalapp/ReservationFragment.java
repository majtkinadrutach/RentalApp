package com.example.rentalapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private TextView textDateFrom, textDateTo;
    private Spinner spinnerCategory;

    public ReservationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        textDateFrom = view.findViewById(R.id.textDateFrom);
        textDateTo = view.findViewById(R.id.textDateTo);
        Button buttonConfirm = view.findViewById(R.id.buttonConfirmReservation);

        String[] categories = {"Laptopy", "Myszki", "Klawiatury", "Tablety", "Zasilacze", "Kable"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(adapter);

        textDateFrom.setOnClickListener(v -> showDatePicker(textDateFrom));

        textDateTo.setOnClickListener(v -> showDatePicker(textDateTo));

        buttonConfirm.setOnClickListener(v -> {
            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            String dateFrom = textDateFrom.getText().toString();
            String dateTo = textDateTo.getText().toString();

            if (dateFrom.contains("Wybierz") || dateTo.contains("Wybierz")) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Błąd")
                        .setMessage("Proszę wybrać daty rezerwacji.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Potwierdzenie")
                    .setMessage("Złożono rezerwację na sprzęt z kategorii:\n" + selectedCategory +
                            "\n\nOd: " + dateFrom + "\nDo: " + dateTo)
                    .setPositiveButton("OK", (dialog, which) -> {
                        NavController navController = Navigation.findNavController(view);
                        navController.popBackStack();
                    })
                    .setCancelable(false)
                    .show();
        });
    }

    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String formattedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    targetTextView.setText(formattedDate);
                }, currentYear, currentMonth, currentDay);

        datePickerDialog.show();
    }
}