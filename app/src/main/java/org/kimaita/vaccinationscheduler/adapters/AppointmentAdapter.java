package org.kimaita.vaccinationscheduler.adapters;

import static org.kimaita.vaccinationscheduler.Utils.dayMonthYearFormatter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.DBViewModel;
import org.kimaita.vaccinationscheduler.R;
import org.kimaita.vaccinationscheduler.models.Appointment;

import java.util.Calendar;

public class AppointmentAdapter extends ListAdapter<Appointment, AppointmentAdapter.ScheduleViewHolder> {

    Calendar cal = Calendar.getInstance();
    Calendar c = Calendar.getInstance();

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
        ImageButton btnAdministered;
        MaterialCardView cardView;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_appointment);
            textDate = itemView.findViewById(R.id.appointment_date);
            textVaccine = itemView.findViewById(R.id.appointment_vaccine);
            btnAdministered = itemView.findViewById(R.id.btn_administered);
        }

        public void bind(Appointment current) {
            cal.setTimeInMillis(System.currentTimeMillis());
            c.setTimeInMillis(current.getVaccinationDate().getTime());
            if (c.compareTo(cal) < 0) {
                if (current.isAdministered()) {

                    btnAdministered.setEnabled(false);
                    btnAdministered.setImageResource(R.drawable.ic_round_done_24);
                } else {

                    btnAdministered.setImageResource(R.drawable.ic_round_error_24);
                }
            }else if(c.compareTo(cal) == 0){
                if(current.isAdministered()){
                    btnAdministered.setEnabled(false);

                    btnAdministered.setImageResource(R.drawable.ic_round_done_24);
                }
                else if (cal.get(Calendar.HOUR_OF_DAY)<21){

                    btnAdministered.setImageResource(R.drawable.ic_pending_24);
                } else {

                    btnAdministered.setImageResource(R.drawable.ic_round_error_24);
                }
            }else{
                btnAdministered.setEnabled(false);
                btnAdministered.setVisibility(View.GONE);
            }

            textDate.setText(dayMonthYearFormatter.format(current.getVaccinationDate()));
            textVaccine.setText(current.getVaccineName());

            btnAdministered.setOnClickListener(v -> new DBViewModel().updateAppointment(current.getAppointmentID()));
        }
    }
}
