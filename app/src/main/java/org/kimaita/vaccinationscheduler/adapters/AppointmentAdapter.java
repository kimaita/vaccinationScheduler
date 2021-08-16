package org.kimaita.vaccinationscheduler.adapters;

import static org.kimaita.vaccinationscheduler.Utils.dayMonthYearFormatter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.R;
import org.kimaita.vaccinationscheduler.models.Appointment;

public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.ScheduleViewHolder> {
    public AppointmentAdapter(@NonNull DiffUtil.ItemCallback<Appointment> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public AppointmentAdapter.ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_appointment, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.ScheduleViewHolder holder, int position) {
        Appointment current = getItem(position);
        holder.bind(current);
    }

    public static class ScheduleDiff extends DiffUtil.ItemCallback<Appointment> {

        @Override
        public boolean areItemsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Appointment oldItem, @NonNull Appointment newItem) {
            return oldItem.getAppointmentID() == newItem.getAppointmentID();
        }
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView textDate, textVaccine;
        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.appointment_date);
            textVaccine = itemView.findViewById(R.id.appointment_vaccine);
        }

        public void bind(Appointment current) {
            textDate.setText(dayMonthYearFormatter.format(current.getVaccinationDate()));
            textVaccine.setText(current.getVaccineName());
        }
    }
}
