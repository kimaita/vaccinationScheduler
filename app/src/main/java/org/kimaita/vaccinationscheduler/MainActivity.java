package org.kimaita.vaccinationscheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrDetails;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_VaccinationScheduler);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        frame = findViewById(R.id.act_main_root);
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

        if (!sharedpreferences.getBoolean(usrCredCurrKey, false)) {
            navController.navigate(R.id.authActivity);
            finish();
        }

        User user = readUserFile();

    }

    private User readUserFile() {
        File file = new File(getApplicationContext().getFilesDir(), usrDetails);
        User usr = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            usr = (User) ois.readObject();
            Log.i("Reading from File", "Read Successfully: "+"Name: "+usr.getUsername()+" "+usr.getEmail());
            ois.close();
        } catch (Exception e) {
            Snackbar.make(frame, "Error Reading from file", Snackbar.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return usr;
    }

}

