package org.kimaita.vaccinationscheduler;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.databinding.FragmentLogInBinding;
import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrDetails;

public class LogInFragment extends Fragment {

    FragmentLogInBinding binding;
    TextInputEditText textNatID, textPIN;
    TextInputLayout layoutID, layoutPIN;
    MaterialButton btnLogIn;
    MaterialTextView textStatus;
    ProgressDialog pDialog;
    private SharedPreferences sharedpreferences;

    public LogInFragment() {        /* Required empty public constructor*/ }

    public static LogInFragment newInstance() {
        return new LogInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(usrCred, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        textNatID = binding.loginNatID;
        textPIN = binding.loginPin;
        layoutID = binding.layoutLoginNatID;
        layoutPIN = binding.layoutLoginPin;
        btnLogIn = binding.loginBtn;
        textStatus = binding.loginTextStatus;

        btnLogIn.setOnClickListener(v -> new FetchAsyncTask().execute());

        return root;
    }

    public class FetchAsyncTask extends AsyncTask<Void, Void, User> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected User doInBackground(Void... voids) {
            int id = Integer.parseInt(String.valueOf(textNatID.getText()));
            User user = new User();
            try {
                ResultSet rs = DatabaseUtils.userDets(id);
                Log.i("FetchAsyncTask", "Loaded Successfully");
                if (rs.next()) {
                    user.setDbID(Integer.parseInt(rs.getString("parent_id")));
                    user.setUsername(rs.getString("parent_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setNatID(Integer.parseInt(rs.getString("nat_id")));
                    user.setPin(Integer.parseInt(rs.getString("user_pin")));
                }
            } catch (SQLException e) {
                cancel(true);
                Log.e("FetchAsyncTask", "Error reading information", e);
                layoutID.setError("Error");
                layoutPIN.setError("Error");
                pDialog.dismiss();
                Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            pDialog.dismiss();
            if (user!=null) {
                if (Integer.parseInt(textPIN.getText().toString()) == user.getPin()) {
                    writeToFile(user);
                    successfulLogin();
                } else {
                    layoutPIN.setError(getString(R.string.wrong_pin));
                }
            } else {
                layoutID.setError(getString(R.string.invalid_username));
                failedLogin();
            }
        }
    }
    
    private void writeToFile(User user) {
        File file = new File(getContext().getFilesDir(), usrDetails);
        try {
            FileOutputStream fs = new FileOutputStream(file);
            ObjectOutputStream ofs = new ObjectOutputStream(fs);
            ofs.writeObject(user);
            fs.close();
        } catch (Exception e) {
            Snackbar.make(getView(), "Error Writing to file", Snackbar.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void failedLogin() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(usrCredCurrKey, false);
        editor.apply();
        //Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.action_logInFragment2_to_signUpFragment2);
    }

    private void successfulLogin() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(usrCredCurrKey, true);
        editor.apply();
        //Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.mainActivity);
    }
}
