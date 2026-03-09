package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

// Importy dla skanera
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import androidx.activity.result.ActivityResultLauncher;

public class DashboardUserFragment extends Fragment {

    public DashboardUserFragment() {
    }

    // skaner użytkownika

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
                    navController.navigate(R.id.action_userDashboard_to_productInfo, bundle);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        // skaner przycisk
        com.google.android.material.floatingactionbutton.FloatingActionButton btnScanner =
                view.findViewById(R.id.buttonUserScanner);

        btnScanner.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("Zeskanuj kod QR sprzętu");
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            options.setOrientationLocked(false);
            barcodeLauncher.launch(options);
        });

        view.findViewById(R.id.buttonAvailable).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "AVAILABLE");
            navController.navigate(R.id.assetListFragment, bundle);
        });

        view.findViewById(R.id.buttonRented).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "RENTED");
            navController.navigate(R.id.assetListFragment, bundle);
        });

        view.findViewById(R.id.buttonDamaged).setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("LIST_TYPE", "DAMAGED");
            navController.navigate(R.id.assetListFragment, bundle);
        });

        view.findViewById(R.id.buttonReserve).setOnClickListener(v -> {
            navController.navigate(R.id.reservationFragment);
        });
    }
}