package org.kimaita.vaccinationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.kimaita.vaccinationscheduler.adapters.HospitalsAdapter;
import org.kimaita.vaccinationscheduler.databinding.FragmentHospitalsBinding;

public class HospitalsFragment extends Fragment {

    FragmentHospitalsBinding binding;
    RecyclerView recyclerHospitals;
    HospitalsAdapter mAdapter;
    DBViewModel viewModel;

    public HospitalsFragment() {
        // Required empty public constructor
    }

    public static HospitalsFragment newInstance() {
        return new HospitalsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHospitalsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        recyclerHospitals = binding.recyclerHospitals;
        mAdapter = new HospitalsAdapter(new HospitalsAdapter.HospitalDiff(), hospital -> {
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("hospital", hospital.getHospital_id());
            intent.putExtra("name",hospital.getHospital_name());
            startActivity(intent);
        });
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerHospitals.getContext(), mLayoutManager.getOrientation());
        recyclerHospitals.addItemDecoration(mDividerItemDecoration);
        recyclerHospitals.setAdapter(mAdapter);
        recyclerHospitals.setLayoutManager(mLayoutManager);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getHospitals().observe(getViewLifecycleOwner(), hospitals -> mAdapter.submitList(hospitals));
    }
}