package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rentalapp.util.TokenManager;
import com.example.rentalapp.viewmodel.AuthViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel scoped do fragmentu — czysty stan przy każdym powrocie do logowania
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        TextInputEditText inputEmployeeId = view.findViewById(R.id.inputEmployeeId);
        TextInputEditText inputPassword   = view.findViewById(R.id.inputPassword);
        Button            buttonLogin     = view.findViewById(R.id.buttonLogin);
        ProgressBar       progress        = view.findViewById(R.id.progressLogin);

        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
        NavController  navController  = Navigation.findNavController(view);

        // ── Kliknięcie "Zaloguj się" ──────────────────────────────────────────
        buttonLogin.setOnClickListener(v -> {
            String employeeId = inputEmployeeId.getText() != null
                    ? inputEmployeeId.getText().toString().trim() : "";
            String password = inputPassword.getText() != null
                    ? inputPassword.getText().toString() : "";
            authViewModel.login(employeeId, password);
        });

        // ── Spinner ładowania ─────────────────────────────────────────────────
        authViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            progress.setVisibility(loading ? View.VISIBLE : View.GONE);
            buttonLogin.setEnabled(!loading);
        });

        // ── Sukces logowania — nawigacja na podstawie roli ───────────────────
        authViewModel.getLoginResult().observe(getViewLifecycleOwner(), response -> {
            if (response == null) return;

            String role = TokenManager.getInstance(requireContext()).getUserRole();

            navigationView.getMenu().clear();
            if ("admin".equals(role)) {
                navigationView.inflateMenu(R.menu.drawer_menu_admin);
                navController.navigate(R.id.action_login_to_adminDashboard);
            } else {
                navigationView.inflateMenu(R.menu.drawer_menu_user);
                navController.navigate(R.id.action_login_to_userDashboard);
            }
        });

        // ── Błąd logowania ────────────────────────────────────────────────────
        authViewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
