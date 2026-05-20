package com.example.rentalapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentalapp.model.Asset;
import com.example.rentalapp.viewmodel.AssetViewModel;
import com.example.rentalapp.viewmodel.RentalViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AssetListFragment extends Fragment implements AssetAdapter.OnActionListener {

    private AssetViewModel  assetViewModel;
    private RentalViewModel rentalViewModel;

    private List<Asset>  currentAssets  = new ArrayList<>();
    private List<String> bulkScanBasket = new ArrayList<>();
    private String       listType       = "ALL";

    private final ActivityResultLauncher<ScanOptions> bulkBarcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) handleScannedItem(result.getContents());
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assetViewModel  = new ViewModelProvider(this).get(AssetViewModel.class);
        rentalViewModel = new ViewModelProvider(this).get(RentalViewModel.class);

        TextView             textListTitle = view.findViewById(R.id.textListTitle);
        RecyclerView         recyclerView  = view.findViewById(R.id.recyclerViewAssets);
        ProgressBar          progress      = view.findViewById(R.id.progressAssets);
        TextView             textEmpty     = view.findViewById(R.id.textEmptyState);
        FloatingActionButton fabBulkScan   = view.findViewById(R.id.fabBulkScan);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) listType = getArguments().getString("LIST_TYPE", "ALL");

        switch (listType) {
            case "AVAILABLE":  textListTitle.setText("Dostępne urządzenia"); break;
            case "DAMAGED":    textListTitle.setText("Zgłoszone uszkodzenia"); break;
            case "RENTED":     textListTitle.setText("Moje wypożyczenia"); break;
            case "RENTED_ALL": textListTitle.setText("Wszystkie wypożyczenia"); break;
            default:           textListTitle.setText("Urządzenia"); break;
        }

        if (listType.equals("AVAILABLE") || listType.equals("RENTED") || listType.equals("RENTED_ALL")) {
            fabBulkScan.setVisibility(View.VISIBLE);
            fabBulkScan.setOnClickListener(v -> { bulkScanBasket.clear(); launchScanner(); });
        }

        // ── AssetViewModel ────────────────────────────────────────────────────
        assetViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progress.setVisibility(loading ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
        });

        assetViewModel.getAssets().observe(getViewLifecycleOwner(), assets -> {
            currentAssets = assets != null ? assets : new ArrayList<>();
            if (currentAssets.isEmpty()) {
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText("Brak sprzętu do wyświetlenia.");
                recyclerView.setVisibility(View.GONE);
            } else {
                textEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new AssetAdapter(currentAssets, this));
            }
        });

        assetViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText(msg);
                recyclerView.setVisibility(View.GONE);
            }
        });

        // ── RentalViewModel ───────────────────────────────────────────────────
        rentalViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                progress.setVisibility(loading ? View.VISIBLE : View.GONE));

        rentalViewModel.getActionSuccess().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                assetViewModel.loadAssets(listType);
            }
        });

        rentalViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty())
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        assetViewModel.loadAssets(listType);
    }

    // ── AssetAdapter.OnActionListener ────────────────────────────────────────

    @Override
    public void onRent(Asset asset) {
        showDatePickerAndRent(asset);
    }

    @Override
    public void onReturn(Asset asset) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Zwróć sprzęt")
                .setMessage("Czy na pewno chcesz zwrócić:\n" + asset.getName() + "?")
                .setPositiveButton("Zwróć", (d, w) ->
                        rentalViewModel.returnByAssetId(asset.getNumericId()))
                .setNegativeButton("Anuluj", null)
                .show();
    }

    @Override
    public void onRepair(Asset asset) {
        Bundle bundle = new Bundle();
        bundle.putString("ASSET_ID", asset.getId());
        bundle.putInt("ASSET_NUMERIC_ID", asset.getNumericId()); // ← potrzebne do API
        Navigation.findNavController(requireView())
                .navigate(R.id.reportFaultFragment, bundle);
    }

    // ── DatePicker ────────────────────────────────────────────────────────────

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
                            .setNegativeButton("Anuluj", null).show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    // ── Bulk scan ─────────────────────────────────────────────────────────────

    private void handleScannedItem(String scannedId) {
        if (bulkScanBasket.contains(scannedId)) {
            showErrorDialog("Duplikat", "Przedmiot #" + scannedId + " już jest w koszyku.");
            return;
        }
        Asset found = findInCurrentList(scannedId);
        if (found == null) {
            showErrorDialog("Nie znaleziono",
                    listType.equals("AVAILABLE")
                            ? "Ten sprzęt nie jest dostępny."
                            : "Ten sprzęt nie jest w bieżącej grupie.");
            return;
        }
        String status = found.getStatus();
        if (listType.equals("AVAILABLE") && !status.equals("Dostępny")) {
            showErrorDialog("Nie można wypożyczyć",
                    "\"" + found.getName() + "\" ma status: " + status + ".");
            return;
        }
        if ((listType.equals("RENTED") || listType.equals("RENTED_ALL"))
                && !status.equals("Wypożyczony")) {
            showErrorDialog("Nie można zwrócić",
                    "\"" + found.getName() + "\" ma status: " + status + ".");
            return;
        }
        bulkScanBasket.add(scannedId);
        showSuccessDialog(scannedId);
    }

    private Asset findInCurrentList(String scannedId) {
        String clean = scannedId.replace("#", "").trim();
        for (Asset a : currentAssets) {
            if (a.getId().replace("#", "").equalsIgnoreCase(clean)) return a;
            if (a.getSerialNumber() != null && a.getSerialNumber().equalsIgnoreCase(clean)) return a;
        }
        return null;
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title).setMessage(message)
                .setPositiveButton("Skanuj ponownie", (d, w) -> launchScanner())
                .setNegativeButton("Zatwierdź obecne", (d, w) -> {
                    if (bulkScanBasket.isEmpty())
                        Toast.makeText(getContext(), "Koszyk pusty.", Toast.LENGTH_SHORT).show();
                    else processBulkAction();
                })
                .setCancelable(false).show();
    }

    private void showSuccessDialog(String scannedId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Dodano: " + scannedId)
                .setMessage("Przedmiot zweryfikowany. Co dalej?")
                .setPositiveButton("Skanuj następny", (d, w) -> launchScanner())
                .setNegativeButton("Zakończ i zatwierdź", (d, w) -> processBulkAction())
                .setCancelable(false).show();
    }

    private void launchScanner() {
        ScanOptions opts = new ScanOptions();
        opts.setPrompt("Skanuj kod QR przedmiotu");
        opts.setOrientationLocked(false);
        bulkBarcodeLauncher.launch(opts);
    }

    private void processBulkAction() {
        if (bulkScanBasket.isEmpty()) return;

        List<Integer> assetIds = new ArrayList<>();
        for (String serial : bulkScanBasket) {
            Asset a = findInCurrentList(serial);
            if (a != null) assetIds.add(a.getNumericId());
        }
        if (assetIds.isEmpty()) {
            Toast.makeText(getContext(), "Brak rozpoznanych ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder summary = new StringBuilder("Sprzęt:\n");
        for (String s : bulkScanBasket) summary.append("• ").append(s).append("\n");

        if (listType.equals("AVAILABLE")) {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (dp, year, month, day) -> {
                        String dueDate = String.format("%04d-%02d-%02d", year, month + 1, day);
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Potwierdź wypożyczenie")
                                .setMessage(summary + "\nData zwrotu: " + dueDate)
                                .setPositiveButton("Zatwierdź", (d, w) ->
                                        rentalViewModel.bulkRent(assetIds, dueDate))
                                .setNegativeButton("Anuluj", null).show();
                    },
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            ).show();
        } else {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Potwierdź zwrot")
                    .setMessage(summary + "\nCzy zatwierdzić zwrot?")
                    .setPositiveButton("Zatwierdź", (d, w) ->
                            rentalViewModel.bulkReturn(assetIds))
                    .setNegativeButton("Anuluj", null).show();
        }
    }
}
