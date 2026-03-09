package com.example.rentalapp;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar topAppBar;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        topAppBar = findViewById(R.id.topAppBar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.dashboardUserFragment);
        topLevelDestinations.add(R.id.dashboardAdminFragment);
        topLevelDestinations.add(R.id.assetListFragment);
        topLevelDestinations.add(R.id.reservationFragment);
        topLevelDestinations.add(R.id.productInfoFragment);
        topLevelDestinations.add(R.id.reportFaultFragment);
        topLevelDestinations.add(R.id.adminReportFragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupWithNavController(topAppBar, navController, appBarConfiguration);

        // menu boczne
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Bundle bundle = new Bundle();

            // menu użytkownika
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
            }
            // menu admina
            else if (id == R.id.menu_admin_dashboard) {
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
            }

            else if (id == R.id.menu_logout) {
                navController.navigate(R.id.loginFragment);
            }

            drawerLayout.close();
            return true;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            topAppBar.setTitle("");

            if (destination.getId() == R.id.loginFragment) {
                topAppBar.setVisibility(View.GONE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                topAppBar.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
    }
}