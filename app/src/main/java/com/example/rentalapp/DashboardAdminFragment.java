package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

// Importy dla skanera QR
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import androidx.activity.result.ActivityResultLauncher;

public class DashboardAdminFragment extends Fragment {

    public DashboardAdminFragment() {
    }

    // skaner admina
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(getContext(), "Anulowano skanowanie", Toast.LENGTH_LONG).show();
                } else {
                    String scannedId = result.getContents();
                    Toast.makeText(getContext(), "Odczytano: " + scannedId, Toast.LENGTH_SHORT).show();

                    Bundle bundle = new Bundle();
                    bundle.putString("SCANNED_ID", scannedId);

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_adminDashboard_to_productInfo, bundle);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // przycisk skanera
        com.google.android.material.floatingactionbutton.FloatingActionButton btnScanner = view.findViewById(R.id.buttonAdminScanner);

        if (btnScanner != null) {
            btnScanner.setOnClickListener(v -> {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                options.setPrompt("Zeskanuj kod QR sprzętu (Tryb Admina)");
                options.setCameraId(0);
                options.setBeepEnabled(true);
                options.setBarcodeImageEnabled(true);
                options.setOrientationLocked(false);
                barcodeLauncher.launch(options);
            });
        }

        // obsługa przycisków

        view.findViewById(R.id.buttonAdminAllItems).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "ALL");
            navController.navigate(R.id.action_adminDashboard_to_assetList, bundle);
        });

        view.findViewById(R.id.buttonAdminAvailable).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "AVAILABLE");
            navController.navigate(R.id.action_adminDashboard_to_assetList, bundle);
        });

        view.findViewById(R.id.buttonAdminRented).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "RENTED_ALL");
            navController.navigate(R.id.action_adminDashboard_to_assetList, bundle);
        });

        view.findViewById(R.id.buttonAdminDamaged).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "DAMAGED");
            navController.navigate(R.id.action_adminDashboard_to_assetList, bundle);
        });

        view.findViewById(R.id.buttonAdminReport).setOnClickListener(v -> {
            navController.navigate(R.id.action_adminDashboard_to_report);
        });

        view.findViewById(R.id.buttonAdminReserve).setOnClickListener(v -> {
            navController.navigate(R.id.action_adminDashboard_to_reservation);
        });
    }
}