package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Utils.isInternetAvailable;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.databinding.FragmentSignUpBinding;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class SignUpFragment extends Fragment {

    FragmentSignUpBinding binding;

    String name, phone, email;
    int id, pin;
    TextInputEditText textName, textEmail, textPhone, textPIN, textNatID;
    TextInputLayout layoutName, layoutEmail, layoutPhone, layoutPIN, layoutID;
    MaterialTextView textStatus;
    MaterialButton btnSignUp, btnLogIn;
    ProgressDialog pDialog;

    public SignUpFragment() {
        // Required empty public constructor
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

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSignUp.setOnClickListener(v -> {
            //internetCheck();
            if (fieldsFilled()) {
                try {
                    boolean isSuccess = new SignUpAsyncTask().execute().get();
                    if(isSuccess){
                        Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(
                                SignUpFragmentDirections.actionSignUpFragment2ToLogInFragment2(Integer.parseInt(textNatID.getText().toString()))
                        );
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                textStatus.setTextColor(Color.RED);
                textStatus.setText(R.string.fill_fields);
            }

        });
        btnLogIn.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(
                        SignUpFragmentDirections.actionSignUpFragment2ToLogInFragment2(0)
                ));

    }

    private void internetCheck() {
        isInternetAvailable().subscribe((hasInternet) -> {
            btnSignUp.setEnabled(hasInternet);
            if (!hasInternet) {
                Snackbar snackbar = Snackbar.make(binding.getRoot(), "No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                internetCheck();
                            }
                        });
                snackbar.show();
            }
        });
    }

    private boolean fieldsFilled() {
        boolean bool = true;
        if (TextUtils.isEmpty(textName.getText())) {
            layoutName.setError(getString(R.string.empty_edittext));
            bool = false;
        }
        if (TextUtils.isEmpty(textEmail.getText())) {
            layoutEmail.setError(getString(R.string.empty_edittext));
            bool = false;
        }
        if (TextUtils.isEmpty(textPhone.getText())) {
            layoutPhone.setError(getString(R.string.empty_edittext));
            bool = false;
        }
        if (TextUtils.isEmpty(textNatID.getText())) {
            layoutID.setError(getString(R.string.empty_edittext));
            bool = false;
        }
        if (TextUtils.isEmpty(textPIN.getText())) {
            layoutPIN.setError(getString(R.string.empty_edittext));
            bool = false;
        }
        return bool;
    }

    @SuppressLint("StaticFieldLeak")
    public class SignUpAsyncTask extends AsyncTask<Void, Void, Boolean> {

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
        protected Boolean doInBackground(Void... voids) {
            name = textName.getText().toString();
            email = textEmail.getText().toString();
            phone = textPhone.getText().toString();
            id = Integer.parseInt(textNatID.getText().toString());
            pin = Integer.parseInt(textPIN.getText().toString());
            boolean isInserted = false;
            Connection con = null;
            try {
                con = DatabaseUtils.createConnection();
                Log.i("Database Connection", "Successful connection");
                pDialog.setMessage("Connecting...");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e("Database Connection", "Failed connection", throwables);
                cancel(true);
                pDialog.dismiss();
                isInserted = false;
                Snackbar.make(getView(), "Can't Connect to the database right now.", Snackbar.LENGTH_LONG).show();
            }
            try {
                assert con != null;
                DatabaseUtils.insertUser(name, email, phone, id, pin, con);
                Log.i("Database Insertion", "Successful insertion");
                pDialog.setMessage("Writing...");
                isInserted = true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e("Database Insertion", "Failed insertion", throwables);
                pDialog.dismiss();
                failedSignUp();
                isInserted = false;
            }
            return isInserted;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar.make(getView(), "Can't Connect to the database right now.", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean bool) {
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

}

