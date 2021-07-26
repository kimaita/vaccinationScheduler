package org.kimaita.vaccinationscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(usrCred, Context.MODE_PRIVATE);

        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.scheduleFragment, R.id.messagesFragment, R.id.vaccinesFragment, R.id.profileFragment)
                .build();
        NavController navController;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.mobile_navigation);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        if(!sharedpreferences.getBoolean(usrCredCurrKey, false)){
            navController.navigate(R.id.authActivity);
            finish();
        }

    }
}

