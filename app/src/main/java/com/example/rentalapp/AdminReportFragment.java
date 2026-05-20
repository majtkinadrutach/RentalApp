package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rentalapp.model.Rental;
import com.example.rentalapp.viewmodel.AdminReportViewModel;

import java.util.List;

public class AdminReportFragment extends Fragment {

    private AdminReportViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdminReportViewModel.class);

        TextView    textRentedCount  = view.findViewById(R.id.textRentedCount);
        TextView    textDamagedCount = view.findViewById(R.id.textDamagedCount);
        TextView    textOverdueCount = view.findViewById(R.id.textOverdueCount);
        TextView    textOverdueList  = view.findViewById(R.id.textOverdueList);
        ProgressBar progress         = view.findViewById(R.id.progressReport);
        Button      buttonPdf        = view.findViewById(R.id.buttonGeneratePdf);

        // ── Obserwuj dane ─────────────────────────────────────────────────────
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                progress.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.getRentedCount().observe(getViewLifecycleOwner(), count ->
                textRentedCount.setText(String.valueOf(count)));

        viewModel.getDamagedCount().observe(getViewLifecycleOwner(), count ->
                textDamagedCount.setText(String.valueOf(count)));

        viewModel.getOverdueList().observe(getViewLifecycleOwner(), rentals -> {
            textOverdueCount.setText(String.valueOf(rentals.size()));
            textOverdueList.setText(buildOverdueText(rentals));
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty())
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        // ── Generuj PDF (TODO: backend endpoint lub lokalna biblioteka) ────────
        buttonPdf.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Generowanie PDF — do implementacji po wdrożeniu backendu.",
                        Toast.LENGTH_SHORT).show());

        viewModel.loadReport();
    }

    /**
     * Formatuje listę przeterminowanych wypożyczeń do czytelnego tekstu.
     * Przykład: "• Jan Kowalski: MacBook Pro M1 (Opóźnienie: 5 dni)"
     */
    private String buildOverdueText(List<Rental> rentals) {
        if (rentals == null || rentals.isEmpty()) {
            return "Brak przetrzymanego sprzętu.";
        }
        StringBuilder sb = new StringBuilder();
        for (Rental r : rentals) {
            sb.append("• ")
              .append(r.getUserDisplayName())
              .append(": ")
              .append(r.getAssetDisplayName());

            int days = r.getOverdueDays();
            if (days > 0) {
                sb.append(" (Opóźnienie: ").append(days).append(" dni)");
            } else {
                sb.append(" (termin: ").append(r.getDueDate()).append(")");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }
}
