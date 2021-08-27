package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_VaccinationScheduler);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedpreferences = getSharedPreferences(usrCred, Context.MODE_PRIVATE);

        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        NavController navController;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.mobile_navigation);
        NavigationUI.setupWithNavController(navView, navController);

        if (!sharedpreferences.getBoolean(usrCredCurrKey, false)) {
            navController.navigate(R.id.authActivity);
            finish();
        }

    }

}

