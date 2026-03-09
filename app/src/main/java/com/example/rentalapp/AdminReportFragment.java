package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminReportFragment extends Fragment {

    public AdminReportFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonGeneratePdf = view.findViewById(R.id.buttonGeneratePdf);
        buttonGeneratePdf.setOnClickListener(v -> {
            //symulacja lol
            Toast.makeText(getContext(), "Trwa generowanie raportu PDF...", Toast.LENGTH_SHORT).show();
        });
    }
}