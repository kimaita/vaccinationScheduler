package org.kimaita.vaccinationscheduler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.jetbrains.annotations.NotNull;
import org.kimaita.vaccinationscheduler.adapters.VaccineAdapter;
import org.kimaita.vaccinationscheduler.databinding.FragmentVaccinesBinding;

public class VaccinesFragment extends Fragment {

    FragmentVaccinesBinding binding;
    DBViewModel viewModel;
    RecyclerView recyclerView;
    VaccineAdapter mAdapter;

    public VaccinesFragment() {
        // Required empty public constructor//
    }

    public static VaccinesFragment newInstance() {
        return new VaccinesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVaccinesBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        recyclerView = binding.recyclerVaccines;
        mAdapter = new VaccineAdapter(new VaccineAdapter.VaccineDiff(), vaccine -> {
            Navigation.findNavController(getView()).navigate(VaccinesFragmentDirections.actionVaccinesFragmentToVaccineDetailsFragment(vaccine.getVaccineDBID()));
        });
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getVaccines().observe(getViewLifecycleOwner(), vaccines -> mAdapter.submitList(vaccines));

    }
}