package org.kimaita.vaccinationscheduler.adapters;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;
import org.kimaita.vaccinationscheduler.R;
import org.kimaita.vaccinationscheduler.models.Message;
import org.kimaita.vaccinationscheduler.models.User;

import static org.kimaita.vaccinationscheduler.Utils.dateFormatter;

public class ChatMessageAdapter extends ListAdapter<Message, ChatMessageAdapter.MsgViewHolder> {

    private final User user;

    public ChatMessageAdapter(@NonNull @NotNull DiffUtil.ItemCallback<Message> diffCallback, User user) {
        super(diffCallback);
        this.user = user;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_chat_message, parent, false);
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MsgViewHolder holder, int position) {
        Message message = getItem(position);
        holder.bind(message, user);
        if(message.isRead()){
            holder.itemView.setBackgroundColor(Color.rgb(98, 0 , 238));
        }
        if(message.getSender() != user.getDbID()){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.gravity = Gravity.START;
            holder.itemView.setBackgroundResource(R.drawable.chat_received_message_bg);
        }
    }
    public static class MessageDiff extends DiffUtil.ItemCallback<Message> {

        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Message oldItem, @NonNull @NotNull Message newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Message oldItem, @NonNull @NotNull Message newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }
    public static class MsgViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView content, tvTime;
        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.chat_message_content);
            tvTime = itemView.findViewById(R.id.chat_content_time);
        }

        public void bind(Message message, User user) {
            content.setText(message.getContent());
            tvTime.setText(dateFormatter.format(message.getTime()));
            if (user.getDbID() == message.getSender()){
                itemView.setBackgroundResource(R.drawable.chat_user_message_bg);
            }else{
                itemView.setBackgroundResource(R.drawable.chat_received_message_bg);
            }
        }
    }
}
