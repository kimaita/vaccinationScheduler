package org.kimaita.vaccinationscheduler;

import android.annotation.SuppressLint;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.databinding.FragmentAddChildBinding;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

public class AddChildFragment extends Fragment {

    FragmentAddChildBinding binding;
    TextInputLayout nameLayout, dobLayout;
    TextInputEditText textName;
    MaterialButton btnAddChild, btnDOB;
    MaterialTextView textStatus;
    MaterialToolbar toolbar;
    int parentID;
    String name;
    Date dob;

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
        textName = binding.addchildName;
        btnDOB = binding.addchildDob;
        btnAddChild = binding.btnAddchild;
        textStatus = binding.addchildTextStatus;
        toolbar = binding.topAppBarAddChild;

        parentID = AddChildFragmentArgs.fromBundle(getArguments()).getParentID();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnDOB.setOnClickListener(v -> setDatePicker().show(requireActivity().getSupportFragmentManager(), "DoB"));

        btnAddChild.setOnClickListener(v -> {
            if (fieldsFilled()) {
                new AddChildAsyncTask().execute();
            } else {
                textStatus.setTextColor(Color.RED);
                textStatus.setText(getString(R.string.fill_fields));
            }
        });

        toolbar.setNavigationOnClickListener(v -> {
                    Navigation.findNavController(getView()).navigate(R.id.action_addChildFragment_to_profileFragment);
                }
        );
    }

    private MaterialDatePicker<Long> setDatePicker() {

        final int CC_EXP_YEARS_COUNT = 10;
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);

        // Build constraints.
        c.set(Calendar.MONTH, mMonth + 9);
        long inNineMonths = c.getTimeInMillis();
        c.set(Calendar.YEAR, mYear - CC_EXP_YEARS_COUNT);
        long tenYears = c.getTimeInMillis();

        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setStart(tenYears)
                .setEnd(inNineMonths);
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Date of Birth")
                .setSelection(System.currentTimeMillis())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            dob = new Date(selection);
            btnDOB.setText(dob.toString());
        });
        return datePicker;
    }

    private boolean fieldsFilled() {
        boolean filled = true;
        if (TextUtils.isEmpty(textName.getText())) {
            nameLayout.setError(getString(R.string.empty_edittext));
            filled = false;
        }
        if (TextUtils.isEmpty(btnDOB.getText())) {
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
            name = textName.getText().toString();

            Connection con = null;

            try {
                con = DatabaseUtils.createConnection();
                Log.i("Database Connection", "Successful connection");
            } catch (SQLException throwables) {
                cancel(true);
                throwables.printStackTrace();
                Log.e("Database Connection", "Failed connection", throwables);
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
        protected void onCancelled() {
            super.onCancelled();
            Snackbar.make(getView(), "Can't Connect to the database right now.", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            Navigation.findNavController(getView()).navigate(R.id.action_addChildFragment_to_profileFragment);
        }
    }

}