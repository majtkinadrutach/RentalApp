package com.example.rentalapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rentalapp.util.TokenManager;
import com.example.rentalapp.viewmodel.FaultReportViewModel;

import java.io.File;

public class ReportFaultFragment extends Fragment {

    private FaultReportViewModel faultViewModel;

    private ImageView imageViewFault;
    private Uri       photoUri;   // URI do pliku tymczasowego z FileProvider

    // ── TakePicture (full-res) — zastępuje stary TakePicturePreview ───────────
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(),
                    success -> {
                        if (success && photoUri != null) {
                            imageViewFault.setImageURI(photoUri);
                        } else {
                            photoUri = null;
                            Toast.makeText(getContext(),
                                    "Anulowano zdjęcie.", Toast.LENGTH_SHORT).show();
                        }
                    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report_fault, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        faultViewModel = new ViewModelProvider(this).get(FaultReportViewModel.class);

        TextView  textFaultAssetId    = view.findViewById(R.id.textFaultAssetId);
        EditText  editFaultDescription = view.findViewById(R.id.editFaultDescription);
        imageViewFault                 = view.findViewById(R.id.imageViewFault);
        Button    buttonTakePhoto      = view.findViewById(R.id.buttonTakePhoto);
        Button    buttonSubmitFault    = view.findViewById(R.id.buttonSubmitFault);

        // Odczytaj przekazany ID sprzętu z bundle
        String displayId  = "";
        int    numericId  = 0;
        if (getArguments() != null) {
            displayId = getArguments().getString("ASSET_ID", "");
            numericId = getArguments().getInt("ASSET_NUMERIC_ID", 0);
        }
        final int finalNumericId = numericId;

        textFaultAssetId.setText("ID Sprzętu: " + (displayId.isEmpty() ? "Nieznany" : displayId));

        // ── Przycisk aparatu ──────────────────────────────────────────────────
        buttonTakePhoto.setOnClickListener(v -> {
            photoUri = createPhotoUri();
            if (photoUri != null) {
                takePictureLauncher.launch(photoUri);
            } else {
                Toast.makeText(getContext(),
                        "Nie można uruchomić aparatu.", Toast.LENGTH_SHORT).show();
            }
        });

        // ── Obserwuj wynik przesłania ─────────────────────────────────────────
        faultViewModel.getActionSuccess().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                navigateBackToDashboard(view);
            }
        });

        faultViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty())
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        faultViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            buttonSubmitFault.setEnabled(!loading);
            buttonTakePhoto.setEnabled(!loading);
        });

        // ── Prześlij zgłoszenie ───────────────────────────────────────────────
        buttonSubmitFault.setOnClickListener(v -> {
            String description = editFaultDescription.getText().toString().trim();

            if (description.isEmpty()) {
                Toast.makeText(getContext(),
                        "Wpisz opis usterki.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (finalNumericId == 0) {
                // Brak ID — nie możemy wysłać do API; pokaż błąd
                Toast.makeText(getContext(),
                        "Brak ID sprzętu. Wróć i zeskanuj sprzęt ponownie.",
                        Toast.LENGTH_LONG).show();
                return;
            }

            faultViewModel.submit(finalNumericId, description, photoUri);
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Tworzy URI do tymczasowego pliku w external cache.
     * FileProvider autoryzuje kamerę do zapisu pod tym URI.
     */
    @Nullable
    private Uri createPhotoUri() {
        try {
            File photoFile = new File(
                    requireContext().getExternalCacheDir(),
                    "fault_photo_" + System.currentTimeMillis() + ".jpg"
            );
            return FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Po wysłaniu zgłoszenia wróć do właściwego dashboardu na podstawie roli.
     * Nie hardkoduje dashboardUserFragment jak poprzednia wersja.
     */
    private void navigateBackToDashboard(View view) {
        NavController nav = Navigation.findNavController(view);
        boolean isAdmin = TokenManager.getInstance(requireContext()).isAdmin();
        int dashboardId = isAdmin
                ? R.id.dashboardAdminFragment
                : R.id.dashboardUserFragment;

        // popBackStack do dashboardu (false = zostaw dashboard na stosie)
        if (!nav.popBackStack(dashboardId, false)) {
            // Jeśli dashboard nie był na stosie — nawiguj bezpośrednio
            nav.navigate(dashboardId);
        }
    }
}
