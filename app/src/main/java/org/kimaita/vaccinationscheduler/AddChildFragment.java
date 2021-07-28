package org.kimaita.vaccinationscheduler;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.databinding.FragmentAddChildBinding;

import java.sql.Connection;
import java.sql.SQLException;

public class AddChildFragment extends Fragment {

    FragmentAddChildBinding binding;
    TextInputLayout nameLayout, dobLayout;
    TextInputEditText textName, textDOB;
    MaterialButton btnAddChild;
    MaterialTextView textStatus;

    public AddChildFragment() { /*Required empty public constructor*/ }

    public static AddChildFragment newInstance() {
        return new AddChildFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddChildBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        nameLayout = binding.layoutAddchildName;
        dobLayout = binding.layoutAddchildDob;
        textName = binding.addchildName;
        textDOB = binding.addchildDob;
        btnAddChild = binding.btnAddchild;
        textStatus = binding.addchildTextStatus;
        btnAddChild.setOnClickListener(v -> {
            if(fieldsFilled()){
                new AddChildAsyncTask().execute();
            }else{
                textStatus.setText(getString(R.string.fill_fields));
            }
        });
        return root;
    }

    private boolean fieldsFilled() {
        boolean filled = true;
        if(TextUtils.isEmpty(textName.getText())){
            nameLayout.setError(getString(R.string.empty_edittext));
            filled = false;
        }
        if(TextUtils.isEmpty(textDOB.getText())){
            dobLayout.setError(getString(R.string.empty_edittext));
            filled = false;
        }
        return filled;
    }


    @SuppressLint("StaticFieldLeak")
    public class AddChildAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String name;
            long dob;
            int parentID = 0;

            name = textName.getText().toString();
            dob = Long.parseLong(textName.getText().toString());
            //parentID = Integer.parseInt()

            Connection con = null;
            try {
                con = DatabaseUtils.createConnection();
                Log.i("Database Connection", "Successful connection");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e("Database Connection", "Failed connection", throwables);
                Snackbar.make(getView(), "Can't Connect to the database right now.", Snackbar.LENGTH_LONG).show();
            }
            try {
                assert con != null;
                DatabaseUtils.insertChild(name, dob, parentID, con);
                Log.i("Database Insertion", "Successful insertion");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e("Database Insertion", "Failed insertion", throwables);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

        }
    }

}