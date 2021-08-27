package org.kimaita.vaccinationscheduler.adapters;

import static org.kimaita.vaccinationscheduler.Utils.dateFormatter;
import static org.kimaita.vaccinationscheduler.Utils.dayFormatter;
import static org.kimaita.vaccinationscheduler.Utils.monthDayFormatter;
import static org.kimaita.vaccinationscheduler.Utils.timeFormatter;

import android.graphics.Typeface;
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
import org.kimaita.vaccinationscheduler.models.Message;

import java.util.Calendar;

public class ChatListAdapter extends ListAdapter<Message, ChatListAdapter.ChatListViewHolder> {

    private final OnItemClickListener clickListener;

    public ChatListAdapter(@NonNull DiffUtil.ItemCallback<Message> diffCallback, OnItemClickListener clickListener) {
        super(diffCallback);
        this.clickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Message message);
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_message, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatListViewHolder holder, int position) {
        Message current = getItem(position);
        holder.bind(current, clickListener);
    }

    public static class MessageListDiff extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Message oldItem, @NonNull @NotNull Message newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Message oldItem, @NonNull @NotNull Message newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }

    public class ChatListViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView textHospitalName, textDate, textContent;
        ShapeableImageView hospitalIcon;

        public ChatListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            hospitalIcon = itemView.findViewById(R.id.hosp_profile_picture);
            textHospitalName = itemView.findViewById(R.id.hosp_name);
            textDate = itemView.findViewById(R.id.text_date);
            textContent = itemView.findViewById(R.id.text_content);
        }

        public void bind(Message current, OnItemClickListener clickListener) {
            textHospitalName.setText(current.getHospitalName());
            Calendar c = Calendar.getInstance();
            Calendar txt = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mWeek = c.get(Calendar.WEEK_OF_YEAR);
            int mDay = c.get(Calendar.DATE);
            txt.setTimeInMillis(current.getTime());
            if(txt.get(Calendar.DATE) == mDay){
                textDate.setText(timeFormatter.format(current.getTime()));
            }else if(txt.get(Calendar.WEEK_OF_YEAR)==mWeek){
                textDate.setText(dayFormatter.format(current.getTime()));
            }else if (txt.get(Calendar.YEAR) == mYear){
                textDate.setText(monthDayFormatter.format(current.getTime()));
            }else{
                textDate.setText(dateFormatter.format(current.getTime()));
            }
            textContent.setText(current.getContent());
            if (!current.isRead() && current.getSender().equalsIgnoreCase("H")){
                textContent.setTypeface(textContent.getTypeface(), Typeface.BOLD);
            }
            itemView.setOnClickListener(v -> clickListener.onItemClick(current));
        }
    }

}
