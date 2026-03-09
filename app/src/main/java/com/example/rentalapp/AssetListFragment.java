package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import androidx.activity.result.ActivityResultLauncher;

import java.util.ArrayList;
import java.util.List;

public class AssetListFragment extends Fragment {

    private final List<String> bulkScanBasket = new ArrayList<>();
    private final List<Asset> allAssets = new ArrayList<>();
    private String listType = "ALL";

    public AssetListFragment() { }

    private final ActivityResultLauncher<ScanOptions> bulkBarcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    String scannedId = result.getContents();

                    // obsługa wyjątków

                    // sprawdzenie duplikatu w obecnej sesji
                    if (bulkScanBasket.contains(scannedId)) {
                        showErrorDialog("Błąd skanowania", "Ten przedmiot (#" + scannedId + ") został już dodany do koszyka.");
                        return;
                    }

                    // szukanie przedmiotu w "bazie"
                    Asset foundAsset = findAssetInDatabase(scannedId);
                    if (foundAsset == null) {
                        showErrorDialog("Nieznany sprzęt", "Przedmiotu o ID " + scannedId + " nie ma w systemie.");
                        return;
                    }

                    // walidacja statusu na podstawie kontekstu (Dostępne vs Wypożyczone)
                    String currentStatus = foundAsset.getStatus();

                    if (listType.equals("AVAILABLE")) {
                        if (!currentStatus.equals("Dostępny")) {
                            String reason = currentStatus.equals("Uszkodzony") ? "jest uszkodzony" : "jest już wypożyczony";
                            showErrorDialog("Nie można wypożyczyć", "Sprzęt " + foundAsset.getName() + " " + reason + ".");
                            return;
                        }
                    }
                    else if (listType.equals("RENTED") || listType.equals("RENTED_ALL")) {
                        if (!currentStatus.equals("Wypożyczony")) {
                            showErrorDialog("Nie można zwrócić", "Nie możesz zwrócić sprzętu, który ma status: " + currentStatus + ".");
                            return;
                        }
                    }

                    bulkScanBasket.add(scannedId);
                    showSuccessDialog(scannedId);
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asset_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textListTitle = view.findViewById(R.id.textListTitle);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAssets);
        FloatingActionButton fabBulkScan = view.findViewById(R.id.fabBulkScan);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            listType = getArguments().getString("LIST_TYPE", "ALL");
        }

        initializeFullDatabase();

        if (listType.equals("AVAILABLE") || listType.equals("RENTED") || listType.equals("RENTED_ALL")) {
            fabBulkScan.setVisibility(View.VISIBLE);
            fabBulkScan.setOnClickListener(v -> {
                bulkScanBasket.clear();
                launchScanner();
            });
        } else {
            fabBulkScan.setVisibility(View.GONE);
        }

        setupViewList(textListTitle, recyclerView);
    }

    // symulacja bazy przedmiotów
    private void initializeFullDatabase() {
        allAssets.clear();
        allAssets.add(new Asset("#LT-001", "Dell Latitude 5520", "Laptop", "Dostępny"));
        allAssets.add(new Asset("#MS-042", "Logitech MX Master", "Myszka", "Dostępny"));
        allAssets.add(new Asset("#TB-011", "iPad Pro 11", "Tablet", "Dostępny"));
        allAssets.add(new Asset("#LT-005", "MacBook Pro M1", "Laptop", "Wypożyczony"));
        allAssets.add(new Asset("#LT-009", "Lenovo ThinkPad", "Laptop", "Uszkodzony"));
    }

    private Asset findAssetInDatabase(String id) {
        for (Asset a : allAssets) {
            if (a.getId().replace("#", "").equals(id.replace("#", ""))) {
                return a;
            }
        }
        return null;
    }

    // wyświetlanie błędów
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Rozumiem", (dialog, which) -> launchScanner())

                .setNegativeButton("Zakończ i zatwierdź obecne", (dialog, which) -> {
                    if (bulkScanBasket.isEmpty()) {
                        Toast.makeText(getContext(), "Koszyk jest pusty, anulowano.", Toast.LENGTH_SHORT).show();
                    } else {
                        processBulkAction();
                    }
                })
                .setCancelable(false)
                .show();
    }

    private void showSuccessDialog(String scannedId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Dodano: " + scannedId)
                .setMessage("Przedmiot poprawnie zweryfikowany. Co dalej?")
                .setPositiveButton("Skanuj następny", (dialog, which) -> launchScanner())
                .setNegativeButton("Zakończ i zatwierdź", (dialog, which) -> processBulkAction())
                .setCancelable(false)
                .show();
    }

    private void launchScanner() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Skanuj kod QR przedmiotu");
        options.setOrientationLocked(false);
        bulkBarcodeLauncher.launch(options);
    }

    private void processBulkAction() {
        if (bulkScanBasket.isEmpty()) return;
        StringBuilder summary = new StringBuilder("Zeskanowane przedmioty:\n");
        for (String id : bulkScanBasket) summary.append("- ").append(id).append("\n");

        new AlertDialog.Builder(requireContext())
                .setTitle("Finalizacja")
                .setMessage(summary.toString() + "\nCzy zatwierdzić operację?")
                .setPositiveButton("Zatwierdź", (dialog, which) -> {
                    Toast.makeText(getContext(), "Zatwierdzono " + bulkScanBasket.size() + " sztuk!", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void setupViewList(TextView title, RecyclerView rv) {
        List<Asset> currentViewList = new ArrayList<>();
        for (Asset a : allAssets) {
            if (listType.equals("AVAILABLE") && a.getStatus().equals("Dostępny")) currentViewList.add(a);
            else if ((listType.equals("RENTED") || listType.equals("RENTED_ALL")) && a.getStatus().equals("Wypożyczony")) currentViewList.add(a);
            else if (listType.equals("DAMAGED") && a.getStatus().equals("Uszkodzony")) currentViewList.add(a);
            else if (listType.equals("ALL")) currentViewList.add(a);
        }
        title.setText(listType.equals("AVAILABLE") ? "Dostępne urządzenia" :
                listType.equals("DAMAGED") ? "Zgłoszone uszkodzenia" : "Urządzenia");
        rv.setAdapter(new AssetAdapter(currentViewList));
    }
}