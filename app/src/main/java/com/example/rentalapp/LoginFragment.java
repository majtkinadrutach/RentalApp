package com.example.rentalapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;

public class LoginFragment extends Fragment {

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonLoginUser = view.findViewById(R.id.buttonLoginUser);
        Button buttonLoginAdmin = view.findViewById(R.id.buttonLoginAdmin);

        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);

        buttonLoginUser.setOnClickListener(v -> {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu_user);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_login_to_userDashboard);
        });

        buttonLoginAdmin.setOnClickListener(v -> {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.drawer_menu_admin);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_login_to_adminDashboard);
        });
    }
}