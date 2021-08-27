package org.kimaita.vaccinationscheduler.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.R;
import org.kimaita.vaccinationscheduler.models.Vaccine;

public class VaccineAdapter extends ListAdapter<Vaccine, VaccineAdapter.VaccineViewHolder> {

    private final OnItemClickListener clickListener;

    public VaccineAdapter(@NonNull DiffUtil.ItemCallback<Vaccine> diffCallback, OnItemClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Vaccine vaccine);
    }

    @NonNull
    @Override
    public VaccineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_vaccine, parent, false);
        return new VaccineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VaccineViewHolder holder, int position) {
        Vaccine current = getItem(position);
        holder.bind(current, clickListener);
    }

    public static class VaccineDiff extends DiffUtil.ItemCallback<Vaccine> {

        @Override
        public boolean areItemsTheSame(@NonNull Vaccine oldItem, @NonNull Vaccine newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Vaccine oldItem, @NonNull Vaccine newItem) {
            return oldItem.getVaccineDBID() == newItem.getVaccineDBID();
        }
    }

    public static class VaccineViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView textVaccineName, textVaccineAdministration;

        VaccineViewHolder(@NonNull View itemView) {
            super(itemView);
            textVaccineName = itemView.findViewById(R.id.item_vaccine_name);
            textVaccineAdministration = itemView.findViewById(R.id.item_vaccine_admin);
        }

        public void bind(Vaccine current, OnItemClickListener clickListener) {
            textVaccineName.setText(current.getVaccineName());
            textVaccineAdministration.setText(current.getVaccineAdministration());
            itemView.setOnClickListener(v -> clickListener.onItemClick(current));
        }
    }
}
