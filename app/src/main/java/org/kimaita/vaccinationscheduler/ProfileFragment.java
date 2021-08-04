package org.kimaita.vaccinationscheduler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;
import org.kimaita.vaccinationscheduler.databinding.FragmentProfileBinding;
import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrDetails;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    MaterialButton btnAddChild, btnLogOUt, btnDeleteAcct;
    MaterialTextView txtViewName, txtViewEmail, txtViewPhone, txtViewNatID;
    SharedPreferences sharedPreferences;
    int parentID;
    File file;
    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireContext().getSharedPreferences(usrCred, Context.MODE_PRIVATE);
        file = new File(getContext().getFilesDir(), usrDetails);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();
        btnAddChild = binding.profileAddchildBtn;
        btnLogOUt = binding.profileLogOut;
        txtViewName = binding.profileName;
        txtViewEmail = binding.profileEmail;
        txtViewPhone = binding.profilePhone;
        txtViewNatID = binding.profileNatID;

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateProfile(readUserFile());

        btnAddChild.setOnClickListener(v -> {
            ProfileFragmentDirections.ActionProfileFragmentToAddChildFragment action =
                    ProfileFragmentDirections.actionProfileFragmentToAddChildFragment(readUserFile().getDbID());

            Navigation.findNavController(getView()).navigate(action);

        });
        btnLogOUt.setOnClickListener(v -> {
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(usrCred, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(usrCredCurrKey, false);
            editor.apply();

            if(!file.delete()){
                Snackbar.make(getView(), "Error Occured Clearing Data", Snackbar.LENGTH_SHORT).show();
            }
            Navigation.findNavController(getView()).navigate(R.id.action_profileFragment_to_authActivity);
        });
    }

    public User readUserFile() {
        User usr = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            usr = (User) ois.readObject();
            ois.close();
        } catch (Exception e) {
            Snackbar.make(getView(), "Error Reading from file", Snackbar.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return usr;
    }

    private void populateProfile(User user){
        txtViewName.setText(user.getUsername());
        txtViewEmail.setText(user.getEmail());
        txtViewPhone.setText(user.getPhoneNumber());
        txtViewNatID.setText(String.valueOf(user.getNatID()));
    }
}