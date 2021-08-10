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
import org.kimaita.vaccinationscheduler.models.Child;

public class ChildAdapter extends ListAdapter<Child, ChildAdapter.ChildViewHolder> {

    public ChildAdapter(DiffUtil.ItemCallback<Child> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_child, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildAdapter.ChildViewHolder holder, int position) {
        Child current = getItem(position);
        holder.bind(current);
    }

    public static class ChildDiff extends DiffUtil.ItemCallback<Child> {

        @Override
        public boolean areItemsTheSame(@NonNull Child oldItem, @NonNull Child newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Child oldItem, @NonNull Child newItem) {
            return oldItem.getChildDBID() == newItem.getChildDBID();
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView name, date;

        ChildViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_child_name);
            date = itemView.findViewById(R.id.item_child_dob);
        }

        private void bind(Child child) {
            name.setText(child.getChildName());
            date.setText(child.getChildDoB().toString());
        }
    }
}
