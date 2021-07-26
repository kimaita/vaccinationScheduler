package org.kimaita.vaccinationscheduler;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.databinding.FragmentSignUpBinding;

import java.sql.Connection;
import java.sql.SQLException;

public class SignUpFragment extends Fragment {

    FragmentSignUpBinding binding;
    public static final String usrCred = "userCredentials";
    EditText textName, textEmail, textPhone, textPIN, textNatID;
    MaterialTextView textStatus;
    MaterialButton btnSignUp, btnLogIn;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
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
        sharedpreferences = getActivity().getSharedPreferences(usrCred, Context.MODE_PRIVATE);
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
        btnSignUp.setOnClickListener(v -> {
            fieldsFilled();
            new SignUpAsyncTask().execute();
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireActivity(), R.id.auth_nav_host_fragment).navigate(R.id.action_signUpFragment2_to_logInFragment2);
            }
        });


        return root;
    }

    private void fieldsFilled() {
        if (TextUtils.isEmpty(textName.getText())) {
            textName.setError(getString(R.string.empty_edittext));
        }
        if (TextUtils.isEmpty(textEmail.getText())) {
            textName.setError(getString(R.string.empty_edittext));
        }
        if (TextUtils.isEmpty(textPhone.getText())) {
            textName.setError(getString(R.string.empty_edittext));
        }
        if (TextUtils.isEmpty(textNatID.getText())) {
            textName.setError(getString(R.string.empty_edittext));
        }
        if (TextUtils.isEmpty(textPIN.getText())) {
            textName.setError(getString(R.string.empty_edittext));
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SignUpAsyncTask extends AsyncTask<String, String, String> {

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage("Registering..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
*/
        @Override
        protected String doInBackground(String... args) {
            String name, phone, email;
            int id, pin;

            name = textName.getText().toString();
            email = textEmail.getText().toString();
            phone = textPhone.getText().toString();
            id = Integer.parseInt(textNatID.getText().toString());
            pin = Integer.parseInt(textPIN.getText().toString());
            Connection con = null;
            //textStatus.setText(name+" "+email+" "+id);
            try {
                con = DatabaseUtils.createConnection();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                DatabaseUtils.insertUser(name, email, phone, id, pin, con);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                //Log.e("Insert Error", "problem",throwables);
                textStatus.setText(throwables.getMessage());
            }
            // Building Parameters
            /*List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("number", phone));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("natID", id));
            params.add(new BasicNameValuePair("pin", pin));
*/
            // getting JSON Object
            // Note that create product url accepts POST method
           /* JSONObject json = jsonParser.makeHttpRequest(url_addParent,
                    "POST", params);
            // check log cat for response
            if (json != null) {
                Log.d("Create Response", json.toString());
                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        // successfully created product
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt(usrCredPINKey, Integer.parseInt(pin));
                        editor.putInt(usrCredNatIDKey, Integer.parseInt(id));
                        editor.apply();
                    } else {
                        // failed to create product
                        failedSignUp();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                failedSignUp();
            }
*/
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            //pDialog.dismiss();
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

