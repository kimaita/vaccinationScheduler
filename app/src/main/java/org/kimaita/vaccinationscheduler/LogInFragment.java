package org.kimaita.vaccinationscheduler;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.kimaita.vaccinationscheduler.databinding.FragmentLogInBinding;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrCredNatIDKey;
import static org.kimaita.vaccinationscheduler.Constants.usrCredPINKey;

public class LogInFragment extends Fragment {

    FragmentLogInBinding binding;
    EditText textNatID, textPIN;
    MaterialButton btnLogIn;
    private SharedPreferences sharedpreferences;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance(String param1, String param2) {
        return new LogInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        textNatID = binding.loginNatID;
        textPIN = binding.loginPin;
        btnLogIn = binding.loginBtn;

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FetchAsyncTask().execute();
            }
        });
        return root;
    }

    public class FetchAsyncTask extends AsyncTask<Void, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            int id = Integer.parseInt(String.valueOf(textPIN.getText()));
            Map<String, String> info = new HashMap<>();
            try {
                ResultSet rs = DatabaseUtils.userDets(id);
                if (rs.next()) {
                    info.put("name", rs.getString("parent_name"));
                    info.put("email", rs.getString("email"));
                    info.put("phone", rs.getString("phone_number"));
                    info.put("nationalID", rs.getString("nat_id"));
                    info.put("pin", rs.getString("user_pin"));
                }
            } catch (SQLException e) {
                Log.e("FetchAsyncTask", "Error reading information", e);
                Toast.makeText(getContext(), "error: "+e, Toast.LENGTH_SHORT).show();
            }
            return info;
        }
        @Override
        protected void onPostExecute(Map<String, String> result) {
            if (!result.isEmpty()) {
                if (textPIN.getText().toString().equals(result.get("pin"))){
                    successfulLogin();
                }
            }else{
                failedLogin();
            }
        }
    }

    private void failedLogin() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(usrCredCurrKey, false);
        editor.apply();
        Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.action_logInFragment2_to_signUpFragment2);
    }

    private void successfulLogin() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(usrCredCurrKey, true);
        editor.apply();
        Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.mainActivity);
    }
}