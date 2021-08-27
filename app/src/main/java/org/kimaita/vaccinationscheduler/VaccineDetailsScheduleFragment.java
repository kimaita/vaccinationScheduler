package org.kimaita.vaccinationscheduler;

import static org.kimaita.vaccinationscheduler.Utils.dayMonthYearFormatter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.adapters.AppointmentAdapter;
import org.kimaita.vaccinationscheduler.databinding.FragmentVaccineDetailsScheduleBinding;
import org.kimaita.vaccinationscheduler.models.Appointment;

public class VaccineDetailsScheduleFragment extends Fragment {

    private static final String ARG_VACCINEID = "param1";
    private static final String ARG_CHILDID = "param2";
    private int vaccineID;
    private int childID;
    FragmentVaccineDetailsScheduleBinding binding;
    RecyclerView recyclerView;
    VaccineChildScheduleAdapter mAdapter;
    LinearLayoutManager layoutManager;
    DBViewModel viewModel;

    public VaccineDetailsScheduleFragment() {
        // Required empty public constructor
    }

    public static VaccineDetailsScheduleFragment newInstance(int childID, int vaxID) {
        VaccineDetailsScheduleFragment fragment = new VaccineDetailsScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_VACCINEID, childID);
        args.putInt(ARG_CHILDID, vaxID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vaccineID = getArguments().getInt(ARG_VACCINEID);
            childID = getArguments().getInt(ARG_CHILDID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentVaccineDetailsScheduleBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerVaccineChild;
        mAdapter = new VaccineChildScheduleAdapter(new AppointmentAdapter.ScheduleDiff());
        viewModel = new ViewModelProvider(this).get(DBViewModel.class);
        recyclerView.setAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getmVaxAppointments(childID, vaccineID).observe(getViewLifecycleOwner(), appointmentArrayList -> mAdapter.submitList(appointmentArrayList));
    }

    private class VaccineChildScheduleAdapter extends ListAdapter<Appointment, VaccineChildScheduleAdapter.ApptViewHolder> {

        protected VaccineChildScheduleAdapter(@NonNull DiffUtil.ItemCallback<Appointment> diffCallback) {
            super(diffCallback);
        }

        @NonNull
        @Override
        public VaccineChildScheduleAdapter.ApptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_appointment, parent, false);
            return new VaccineChildScheduleAdapter.ApptViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VaccineChildScheduleAdapter.ApptViewHolder holder, int position) {
            Appointment current = getItem(position);
            holder.bind(current);
        }

        private class ApptViewHolder extends RecyclerView.ViewHolder {
            MaterialTextView textDate, textStatus;
            public ApptViewHolder(@NonNull View itemView) {
                super(itemView);
                textDate = itemView.findViewById(R.id.appointment_date);
                textStatus = itemView.findViewById(R.id.appointment_vaccine);
            }

            public void bind(Appointment current) {
                textDate.setText(dayMonthYearFormatter.format(current.getVaccinationDate()));
                if (current.isAdministered()){
                    textStatus.setText("Given");
                }else{
                    textStatus.setText("Not Given");
                }
            }
        }
    }


}