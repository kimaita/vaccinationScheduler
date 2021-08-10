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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;
import org.kimaita.vaccinationscheduler.adapters.ChildAdapter;
import org.kimaita.vaccinationscheduler.databinding.FragmentProfileBinding;
import org.kimaita.vaccinationscheduler.models.User;

import java.io.File;

import static org.kimaita.vaccinationscheduler.Constants.usrCred;
import static org.kimaita.vaccinationscheduler.Constants.usrCredCurrKey;
import static org.kimaita.vaccinationscheduler.Constants.usrDetails;
import static org.kimaita.vaccinationscheduler.Utils.readUserFile;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    ExtendedFloatingActionButton btnAddChild;
    MaterialButton  btnLogOUt, btnDeleteAcct;
    MaterialTextView txtViewName, txtViewEmail, txtViewPhone, txtViewNatID;
    RecyclerView recyclerView;
    ChildAdapter mAdapter;
    SharedPreferences sharedPreferences;
    DBViewModel viewModel;
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
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        btnAddChild = binding.profileAddchildBtn;
        btnLogOUt = binding.profileLogOut;
        txtViewName = binding.profileName;
        txtViewEmail = binding.profileEmail;
        txtViewPhone = binding.profilePhone;
        txtViewNatID = binding.profileNatID;
        recyclerView = binding.recyclerChildren;
        mAdapter = new ChildAdapter(new ChildAdapter.ChildDiff());
        recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        populateProfile(readUserFile(file));

        viewModel.getChildren(readUserFile(file).getDbID()).observe(getViewLifecycleOwner(), children -> mAdapter.submitList(children));

        btnAddChild.setOnClickListener(v -> {
            ProfileFragmentDirections.ActionProfileFragmentToAddChildFragment action =
                    ProfileFragmentDirections.actionProfileFragmentToAddChildFragment(readUserFile(file).getDbID());

            Navigation.findNavController(getView()).navigate(action);

        });
        btnLogOUt.setOnClickListener(v -> {
            SharedPreferences sharedpreferences = getActivity().getSharedPreferences(usrCred, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(usrCredCurrKey, false);
            editor.apply();

            if (!file.delete()) {
                Snackbar.make(getView(), "Error Occured Clearing Data", Snackbar.LENGTH_SHORT).show();
            }
            Navigation.findNavController(getView()).navigate(R.id.action_profileFragment_to_authActivity);
        });
    }

    private void populateProfile(User user) {
        txtViewName.setText(user.getUsername());
        txtViewEmail.setText(user.getEmail());
        txtViewPhone.setText(user.getPhoneNumber());
        txtViewNatID.setText(String.valueOf(user.getNatID()));
    }
}