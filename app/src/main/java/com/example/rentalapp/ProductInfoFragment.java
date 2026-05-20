package com.example.rentalapp;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rentalapp.model.Asset;
import com.example.rentalapp.viewmodel.AssetViewModel;
import com.example.rentalapp.viewmodel.RentalViewModel;

import java.util.Calendar;

public class ProductInfoFragment extends Fragment {

    private AssetViewModel  assetViewModel;
    private RentalViewModel rentalViewModel;
    private Asset           currentAsset;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assetViewModel  = new ViewModelProvider(this).get(AssetViewModel.class);
        rentalViewModel = new ViewModelProvider(this).get(RentalViewModel.class);

        TextView textInfoName     = view.findViewById(R.id.textInfoName);
        TextView textInfoCategory = view.findViewById(R.id.textInfoCategory);
        TextView textInfoStatus   = view.findViewById(R.id.textInfoStatus);
        Button   buttonInfoAction = view.findViewById(R.id.buttonInfoAction);
        Button   buttonInfoReport = view.findViewById(R.id.buttonInfoReport);

        NavController navController = Navigation.findNavController(view);

        final String scannedId = (getArguments() != null)
                ? getArguments().getString("SCANNED_ID", "") : "";

        textInfoName.setText("Wczytywanie...");
        textInfoCategory.setText("");
        textInfoStatus.setText("");
        buttonInfoAction.setEnabled(false);

        // ── Obserwuj dane sprzętu ─────────────────────────────────────────────
        assetViewModel.getSelectedAsset().observe(getViewLifecycleOwner(), asset -> {
            if (asset == null) return;
            currentAsset = asset;
            bindAsset(asset, textInfoName, textInfoCategory, textInfoStatus, buttonInfoAction);
        });

        assetViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                textInfoName.setText("Nieznany sprzęt");
                textInfoCategory.setText("ID: " + scannedId);
                textInfoStatus.setText("Brak w systemie");
                textInfoStatus.setTextColor(Color.parseColor("#FF0000"));
                buttonInfoAction.setText("Brak akcji");
                buttonInfoAction.setEnabled(false);
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });

        // ── Obserwuj wynik wypożyczenia/zwrotu ────────────────────────────────
        rentalViewModel.getActionSuccess().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                navController.popBackStack();
            }
        });

        rentalViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty())
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        // ── Załaduj dane ──────────────────────────────────────────────────────
        if (!scannedId.isEmpty()) {
            assetViewModel.loadAssetBySerial(scannedId);
        } else {
            textInfoName.setText("Brak ID sprzętu");
            textInfoStatus.setText("Zeskanuj sprzęt ponownie.");
        }

        // ── Przycisk "Zgłoś usterkę" — przekazuje oba ID ─────────────────────
        buttonInfoReport.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("ASSET_ID", scannedId);
            // Przekaż też numeryczne ID dla API (dostępne po załadowaniu asset)
            if (currentAsset != null) {
                bundle.putInt("ASSET_NUMERIC_ID", currentAsset.getNumericId());
            }
            navController.navigate(R.id.action_productInfo_to_reportFault, bundle);
        });

        // ── Przycisk akcji (Wypożycz / Zwróć) ────────────────────────────────
        buttonInfoAction.setOnClickListener(v -> {
            if (currentAsset == null) return;
            switch (currentAsset.getStatus()) {
                case "Dostępny":  showDatePickerAndRent(currentAsset); break;
                case "Wypożyczony": confirmReturn(currentAsset);       break;
            }
        });
    }

    private void bindAsset(Asset asset, TextView name, TextView category,
                            TextView status, Button actionBtn) {
        name.setText(asset.getName() + " (" + asset.getId() + ")");
        category.setText("Kategoria: " + asset.getCategory());
        status.setText("Status: " + asset.getStatus());

        switch (asset.getStatus()) {
            case "Dostępny":
                status.setTextColor(Color.parseColor("#008000"));
                actionBtn.setText("Wypożycz sprzęt");
                actionBtn.setEnabled(true);
                break;
            case "Wypożyczony":
                status.setTextColor(Color.parseColor("#FFA500"));
                actionBtn.setText("Zwróć sprzęt");
                actionBtn.setEnabled(true);
                break;
            case "Uszkodzony":
                status.setTextColor(Color.parseColor("#D32F2F"));
                actionBtn.setText("Sprzęt uszkodzony");
                actionBtn.setEnabled(false);
                break;
            default:
                status.setTextColor(Color.GRAY);
                actionBtn.setText("Brak akcji");
                actionBtn.setEnabled(false);
        }
    }

    private void showDatePickerAndRent(Asset asset) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(),
                (dp, year, month, day) -> {
                    String dueDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Potwierdź wypożyczenie")
                            .setMessage("Wypożyczasz: " + asset.getName()
                                    + "\nData zwrotu: " + dueDate)
                            .setPositiveButton("Wypożycz", (d, w) ->
                                    rentalViewModel.createRental(asset.getNumericId(), dueDate))
                            .setNegativeButton("Anuluj", null)
                            .show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void confirmReturn(Asset asset) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Zwróć sprzęt")
                .setMessage("Czy na pewno chcesz zwrócić:\n" + asset.getName() + "?")
                .setPositiveButton("Zwróć", (d, w) ->
                        rentalViewModel.returnByAssetId(asset.getNumericId()))
                .setNegativeButton("Anuluj", null)
                .show();
    }
}
