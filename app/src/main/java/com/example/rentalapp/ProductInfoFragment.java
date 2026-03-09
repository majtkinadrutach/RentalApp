package com.example.rentalapp;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ProductInfoFragment extends Fragment {

    public ProductInfoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textInfoName = view.findViewById(R.id.textInfoName);
        TextView textInfoCategory = view.findViewById(R.id.textInfoCategory);
        TextView textInfoStatus = view.findViewById(R.id.textInfoStatus);
        Button buttonInfoAction = view.findViewById(R.id.buttonInfoAction);
        Button buttonInfoReport = view.findViewById(R.id.buttonInfoReport);

        final String scannedId = (getArguments() != null)
                ? getArguments().getString("SCANNED_ID", "Brak ID")
                : "Brak ID";

        if (scannedId.contains("LT-001")) {
            textInfoName.setText("Dell Latitude 5520 (" + scannedId + ")");
            textInfoCategory.setText("Kategoria: Laptop");
            textInfoStatus.setText("Status: Dostępny");
            textInfoStatus.setTextColor(Color.parseColor("#008000"));
            buttonInfoAction.setText("Wypożycz sprzęt");
            buttonInfoAction.setEnabled(true);

        } else if (scannedId.contains("LT-005")) {
            textInfoName.setText("MacBook Pro M1 (" + scannedId + ")");
            textInfoCategory.setText("Kategoria: Laptop");
            textInfoStatus.setText("Status: Wypożyczony");
            textInfoStatus.setTextColor(Color.parseColor("#FFA500"));
            buttonInfoAction.setText("Zwróć sprzęt");
            buttonInfoAction.setEnabled(true);

        } else {
            textInfoName.setText("Nieznany sprzęt (" + scannedId + ")");
            textInfoCategory.setText("Kategoria: Nieznana");
            textInfoStatus.setText("Status: Brak w systemie");
            textInfoStatus.setTextColor(Color.parseColor("#FF0000"));
            buttonInfoAction.setText("Brak akcji");
            buttonInfoAction.setEnabled(false);
        }

        buttonInfoAction.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Wykonano: " + buttonInfoAction.getText(), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack();
        });

        buttonInfoReport.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            Bundle bundle = new Bundle();
            bundle.putString("ASSET_ID", scannedId);
            navController.navigate(R.id.action_productInfo_to_reportFault, bundle);
        });
    }
}