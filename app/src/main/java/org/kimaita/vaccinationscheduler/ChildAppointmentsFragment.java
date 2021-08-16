package org.kimaita.vaccinationscheduler;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.kimaita.vaccinationscheduler.adapters.AppointmentAdapter;
import org.kimaita.vaccinationscheduler.databinding.FragmentChildAppointmentsBinding;
import org.kimaita.vaccinationscheduler.models.Appointment;

import java.util.ArrayList;
import java.util.Calendar;

public class ChildAppointmentsFragment extends Fragment {

    private static final String ARG_CHILD_ID = "param1";
    private int childId;
    DBViewModel viewModel;
    RecyclerView recyclerView;
    AppointmentAdapter mAdapter;
    FragmentChildAppointmentsBinding binding;
    LinearLayoutManager layoutManager;

    public ChildAppointmentsFragment() {
        // Required empty public constructor
    }

    public static ChildAppointmentsFragment newInstance(int child) {
        ChildAppointmentsFragment fragment = new ChildAppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHILD_ID, child);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            childId = getArguments().getInt(ARG_CHILD_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChildAppointmentsBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerSchedule;
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        mAdapter = new AppointmentAdapter(new AppointmentAdapter.ScheduleDiff());
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getmAppointments(childId).observe(getViewLifecycleOwner(), appointments -> {
            mAdapter.submitList(appointments);
            layoutManager.scrollToPositionWithOffset(pos(appointments), 5);
        });
    }

    private static int pos(ArrayList<Appointment> appointmentArrayList){
        int position = 0;
        Calendar cal = Calendar.getInstance();
        Calendar apt = Calendar.getInstance();
        int mYear = cal.get(Calendar.YEAR);
        int mDay = cal.get(Calendar.DAY_OF_YEAR);
        for(Appointment ap: appointmentArrayList){
            apt.setTimeInMillis(ap.getVaccinationDate().getTime());
            if(apt.get(Calendar.YEAR)>=mYear){
                if(apt.get(Calendar.DAY_OF_YEAR)>=mDay){
                    position = appointmentArrayList.indexOf(ap);
                    break;
                }
            }
        }

        return position;
    }
}