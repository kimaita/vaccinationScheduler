package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrDetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import java.util.concurrent.ExecutionException;

public class LogInFragment extends Fragment {

    FragmentLogInBinding binding;
    TextInputEditText textNatID, textPIN;
    TextInputLayout layoutID, layoutPIN;
    MaterialButton btnLogIn, btnSignUp;
    MaterialTextView textStatus;
    ProgressDialog pDialog;
    private SharedPreferences sharedpreferences;

    public LogInFragment() {        /* Required empty public constructor*/ }

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
        btnSignUp = binding.signUpLoginBtn;
        textStatus = binding.loginTextStatus;

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (LogInFragmentArgs.fromBundle(getArguments()).getSignUpNatID() != 0) {
            textNatID.setText(String.valueOf(LogInFragmentArgs.fromBundle(getArguments()).getSignUpNatID()));
        }

        btnLogIn.setOnClickListener(v -> {
            if (!isFieldsFilled()) {
                textStatus.setTextColor(Color.RED);
                textStatus.setText(R.string.fill_fields);
            } else {
                try {
                    User u = new FetchAsyncTask().execute().get();
                    if (u != null) {
                        Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.mainActivity);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        btnSignUp.setOnClickListener(v -> {
            Navigation.findNavController(getView()).navigate(R.id.action_logInFragment2_to_signUpFragment2);
        });
    }

    private boolean isFieldsFilled(){
        boolean bool = true;
        if (textNatID.getText().toString().isEmpty()){
            bool = false;
            layoutID.setError(getString(R.string.empty_edittext));
        }
        if(textPIN.getText().toString().isEmpty()) {
            bool = false;
            layoutPIN.setError(getString(R.string.empty_edittext));
        }
        return bool;
    }

    public class FetchAsyncTask extends AsyncTask<Void, Void, User> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected User doInBackground(Void... voids) {
            int id = Integer.parseInt(String.valueOf(textNatID.getText()));
            User user = new User();
            try {
                ResultSet rs = DatabaseUtils.userDets(id);
                pDialog.setMessage("Verifying...");
                Log.i("FetchAsyncTask", "Loaded Successfully");
                if (rs.next()) {
                    user.setDbID(rs.getInt("parent_id"));
                    user.setUsername(rs.getString("parent_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setNatID(rs.getInt("nat_id"));
                    user.setPin(rs.getInt("user_pin"));
                }
            } catch (SQLException e) {
                cancel(true);
                Log.e("FetchAsyncTask", "Error reading information", e);
                layoutID.setError("Error");
                layoutPIN.setError("Error");
                pDialog.dismiss();
                Toast.makeText(getContext(), "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            pDialog.dismiss();
            if (user != null) {
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

    public void writeToFile(User user) {
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
    }

    private void successfulLogin() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(usrCredCurrKey, true);
        editor.apply();
    }
}
