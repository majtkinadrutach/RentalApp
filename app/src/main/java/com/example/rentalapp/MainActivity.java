package com.example.rentalapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rentalapp.util.SessionManager;
import com.example.rentalapp.util.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout    drawerLayout;
    private NavigationView  navigationView;
    private MaterialToolbar topAppBar;
    private NavController   navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout   = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        topAppBar      = findViewById(R.id.topAppBar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // ── Top-level destinations ────────────────────────────────────────────
        Set<Integer> topLevel = new HashSet<>();
        topLevel.add(R.id.dashboardUserFragment);
        topLevel.add(R.id.dashboardAdminFragment);
        topLevel.add(R.id.assetListFragment);
        topLevel.add(R.id.reservationFragment);
        topLevel.add(R.id.productInfoFragment);
        topLevel.add(R.id.reportFaultFragment);
        topLevel.add(R.id.adminReportFragment);

        AppBarConfiguration appBarConfig = new AppBarConfiguration.Builder(topLevel)
                .setOpenableLayout(drawerLayout)
                .build();
        NavigationUI.setupWithNavController(topAppBar, navController, appBarConfig);

        // ── Globalna obsługa wygaśnięcia sesji (401) ─────────────────────────
        // AuthInterceptor wywołuje SessionManager.notifySessionExpired() z wątku
        // sieciowego. Tutaj obserwujemy i reagujemy na głównym wątku UI.
        SessionManager.getInstance().getSessionExpired().observe(this, expired -> {
            if (expired != null && expired) {
                SessionManager.getInstance().reset();
                Toast.makeText(this,
                        "Sesja wygasła. Zaloguj się ponownie.",
                        Toast.LENGTH_LONG).show();
                handleLogout();
            }
        });

        // ── Persystencja sesji ────────────────────────────────────────────────
        TokenManager tokenManager = TokenManager.getInstance(this);
        if (tokenManager.hasToken()) {
            navigationView.getMenu().clear();
            if (tokenManager.isAdmin()) {
                navigationView.inflateMenu(R.menu.drawer_menu_admin);
                navController.navigate(R.id.dashboardAdminFragment);
            } else {
                navigationView.inflateMenu(R.menu.drawer_menu_user);
                navController.navigate(R.id.dashboardUserFragment);
            }
        }

        // ── Menu boczne ───────────────────────────────────────────────────────
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Bundle bundle = new Bundle();

            if (id == R.id.menu_user_dashboard) {
                navController.navigate(R.id.dashboardUserFragment);
            } else if (id == R.id.menu_user_available) {
                bundle.putString("LIST_TYPE", "AVAILABLE");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_user_rented) {
                bundle.putString("LIST_TYPE", "RENTED");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_user_damaged) {
                bundle.putString("LIST_TYPE", "DAMAGED");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_user_reserve) {
                navController.navigate(R.id.reservationFragment);
            } else if (id == R.id.menu_admin_dashboard) {
                navController.navigate(R.id.dashboardAdminFragment);
            } else if (id == R.id.menu_admin_all) {
                bundle.putString("LIST_TYPE", "ALL");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_admin_available) {
                bundle.putString("LIST_TYPE", "AVAILABLE");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_admin_rented) {
                bundle.putString("LIST_TYPE", "RENTED_ALL");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_admin_damaged) {
                bundle.putString("LIST_TYPE", "DAMAGED");
                navController.navigate(R.id.assetListFragment, bundle);
            } else if (id == R.id.menu_admin_report) {
                navController.navigate(R.id.adminReportFragment);
            } else if (id == R.id.menu_admin_reserve) {
                navController.navigate(R.id.reservationFragment);
            } else if (id == R.id.menu_logout) {
                handleLogout();
            }

            drawerLayout.close();
            return true;
        });

        // ── Toolbar ───────────────────────────────────────────────────────────
        navController.addOnDestinationChangedListener((ctrl, dest, args) -> {
            topAppBar.setTitle("");
            if (dest.getId() == R.id.loginFragment) {
                topAppBar.setVisibility(View.GONE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                topAppBar.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }

    private void handleLogout() {
        TokenManager.getInstance(this).clear();
        navigationView.getMenu().clear();

        // Wróć do loginFragment i wyczyść cały back stack
        navController.popBackStack(R.id.loginFragment, false);
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() != R.id.loginFragment) {
            navController.navigate(R.id.loginFragment);
        }
    }
}
