package org.kimaita.vaccinationscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        NavController navController;
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.auth_nav_host_fragment);
        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.auth_navigation);

        SharedPreferences sharedpreferences = getSharedPreferences(usrCred, Context.MODE_PRIVATE);

        if(sharedpreferences.getBoolean(usrCredCurrKey, false)){
            navController.navigate(R.id.logInFragment2);
        }
    }
}