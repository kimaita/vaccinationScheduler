package org.kimaita.vaccinationscheduler.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;
import org.kimaita.vaccinationscheduler.R;
import org.kimaita.vaccinationscheduler.models.Hospital;

public class HospitalsAdapter extends ListAdapter<Hospital, HospitalsAdapter.HospitalViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Hospital hospital);
    }

    private final OnItemClickListener listener;

    public HospitalsAdapter(@NonNull DiffUtil.ItemCallback<Hospital> diffCallback, OnItemClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_hospital, parent, false);
        return new HospitalsAdapter.HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital current = getItem(position);
        holder.bind(current, listener);
    }

    public static class HospitalDiff extends DiffUtil.ItemCallback<Hospital> {

        @Override
        public boolean areItemsTheSame(@NonNull Hospital oldItem, @NonNull Hospital newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Hospital oldItem, @NonNull Hospital newItem) {
            return oldItem.getHospital_id() == newItem.getHospital_id();
        }
    }

    public class HospitalViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView textHospitalName;
        ShapeableImageView hospitalIcon;

        public HospitalViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textHospitalName = itemView.findViewById(R.id.hosp_list_name);
            hospitalIcon = itemView.findViewById(R.id.hosp_list_profile_picture);
        }

        public void bind(Hospital current, OnItemClickListener listener) {
            textHospitalName.setText(current.getHospital_name());
            itemView.setOnClickListener(v -> listener.onItemClick(current));
        }
    }
}
