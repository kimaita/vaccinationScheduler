package org.kimaita.vaccinationscheduler;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.databinding.FragmentSignUpBinding;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrCredNatIDKey;
import static org.kimaita.vaccinationscheduler.Constants.usrCredPINKey;
import static org.kimaita.vaccinationscheduler.NetworkUtils.isInternetAvailable;

public class SignUpFragment extends Fragment {

    FragmentSignUpBinding binding;

    String name, phone, email;
    int id, pin;
    TextInputEditText textName, textEmail, textPhone, textPIN, textNatID;
    TextInputLayout layoutName, layoutEmail, layoutPhone, layoutPIN, layoutID;
    MaterialTextView textStatus;
    MaterialButton btnSignUp, btnLogIn;
    ProgressDialog pDialog;
    SharedPreferences sharedpreferences;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        textName = binding.signupName;
        textEmail = binding.signupEmail;
        textNatID = binding.signupNatId;
        textPIN = binding.signupPin;
        textPhone = binding.signupPhone;
        btnSignUp = binding.btnSignUp;
        btnLogIn = binding.loginSignUpBtn;
        textStatus = binding.textStatus;
        layoutName = binding.layoutSignupName;
        layoutPhone = binding.layoutSignupPhone;
        layoutEmail = binding.layoutSignupEmail;
        layoutID = binding.layoutSignupNatId;
        layoutPIN = binding.layoutSignupPin;

        name = textName.getText().toString();
        email = textEmail.getText().toString();
        phone = textPhone.getText().toString();
        id = Integer.parseInt(textNatID.getText().toString());
        pin = Integer.parseInt(textPIN.getText().toString());
        
        isInternetAvailable().subscribe((hasInternet) -> {
            btnSignUp.setEnabled(hasInternet);
        });

        btnSignUp.setOnClickListener(v -> {
            if(fieldsFilled()){
                new SignUpAsyncTask().execute();
            }else{
                textStatus.setTextColor(Color.RED);
                textStatus.setText(R.string.fill_fields);
            }

        });
        btnLogIn.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.action_signUpFragment2_to_logInFragment2));

        return root;
    }

    private boolean fieldsFilled() {

        if (TextUtils.isEmpty(textName.getText())) {
            layoutName.setError(getString(R.string.empty_edittext));
            return false;
        }
        if (TextUtils.isEmpty(textEmail.getText())) {
            layoutEmail.setError(getString(R.string.empty_edittext));
            return false;
        }
        if (TextUtils.isEmpty(textPhone.getText())) {
            layoutPhone.setError(getString(R.string.empty_edittext));
            return false;
        }
        if (TextUtils.isEmpty(textNatID.getText())) {
            layoutID.setError(getString(R.string.empty_edittext));
            return false;
        }
        if (TextUtils.isEmpty(textPIN.getText())) {
            layoutPIN.setError(getString(R.string.empty_edittext));
            return false;
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public class SignUpAsyncTask extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String... args) {

            Connection con = null;
            try {
                con = DatabaseUtils.createConnection();
                Log.i("Database Connection", "Successful connection");
                pDialog.setMessage("Connecting...");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e("Database Connection", "Failed connection", throwables);
                pDialog.dismiss();
                Snackbar.make(getView(), "Can't Connect to the database right now.", Snackbar.LENGTH_LONG).show();
            }
            try {
                assert con != null;
                DatabaseUtils.insertUser(name, email, phone, id, pin, con);
                Log.i("Database Insertion", "Successful insertion");
                pDialog.setMessage("Writing...");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e("Database Insertion", "Failed insertion", throwables);
                pDialog.dismiss();
                failedSignUp();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
        }
    }

    private void failedSignUp() {
        Snackbar snackbar = Snackbar.make(getView(), "Failed to Register", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new SignUpAsyncTask().execute();
                    }
                });
        snackbar.show();
    }

    private void successfulSignUp() {
        sharedpreferences = getActivity().getSharedPreferences(usrCred, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(usrCredNatIDKey, id);
        editor.putInt(usrCredPINKey, pin);
        editor.apply();
        Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.action_signUpFragment2_to_logInFragment2);
    }
}

