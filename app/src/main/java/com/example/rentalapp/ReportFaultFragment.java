package com.example.rentalapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class ReportFaultFragment extends Fragment {

    private ImageView imageViewFault;

    public ReportFaultFragment() {
    }

    // aparacik
    private final ActivityResultLauncher<Void> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            result -> {
                if (result != null) {
                    imageViewFault.setImageBitmap(result);
                } else {
                    Toast.makeText(getContext(), "Anulowano robienie zdjęcia", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_fault, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textFaultAssetId = view.findViewById(R.id.textFaultAssetId);
        EditText editFaultDescription = view.findViewById(R.id.editFaultDescription);
        imageViewFault = view.findViewById(R.id.imageViewFault);
        Button buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);
        Button buttonSubmitFault = view.findViewById(R.id.buttonSubmitFault);

        final String assetId = (getArguments() != null)
                ? getArguments().getString("ASSET_ID", "Nieznany sprzęt")
                : "Nieznany sprzęt";

        textFaultAssetId.setText("ID Sprzętu: " + assetId);

        buttonTakePhoto.setOnClickListener(v -> {
            takePictureLauncher.launch(null);
        });

        buttonSubmitFault.setOnClickListener(v -> {
            String description = editFaultDescription.getText().toString().trim();

            if (description.isEmpty()) {
                Toast.makeText(getContext(), "Proszę wpisać opis usterki", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Zgłoszenie wysłane! (" + assetId + ")", Toast.LENGTH_LONG).show();

            NavController navController = Navigation.findNavController(view);
            navController.popBackStack(R.id.dashboardUserFragment, false);
        });
    }
}