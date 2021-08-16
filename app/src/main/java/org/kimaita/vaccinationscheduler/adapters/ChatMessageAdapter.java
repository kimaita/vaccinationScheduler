package org.kimaita.vaccinationscheduler.adapters;

import static org.kimaita.vaccinationscheduler.Utils.timeFormatter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import org.kimaita.vaccinationscheduler.R;
import org.kimaita.vaccinationscheduler.models.ChatDate;
import org.kimaita.vaccinationscheduler.models.ChatMessage;
import org.kimaita.vaccinationscheduler.models.Message;

public class ChatMessageAdapter extends ListAdapter<ChatMessage, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 3;

    public ChatMessageAdapter(@NonNull DiffUtil.ItemCallback<ChatMessage> diffCallback) {
        super(diffCallback);
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        int viewType;
        ChatMessage chatMessage = getItem(position);
        viewType = chatMessage.getType();
        if (viewType == 1) {
            Message msg = (Message) chatMessage;
            if (msg.getSender().equals("P")) {
                // If the current user is the sender of the message
                viewType = VIEW_TYPE_MESSAGE_SENT;
            } else {
                viewType = VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case VIEW_TYPE_MESSAGE_SENT:
                View v1 = inflater.inflate(R.layout.recycler_item_chat_sent_message, parent, false);
                viewHolder = new UserTextViewHolder(v1);
                break;

            case VIEW_TYPE_MESSAGE_RECEIVED:
                View v3 = inflater.inflate(R.layout.recycler_item_chat_rec_message, parent, false);
                viewHolder = new ReceivedTextViewHolder(v3);
                break;

            case ChatMessage.TYPE_DATE:
                View v2 = inflater.inflate(R.layout.recycler_item_chat_date, parent, false);
                viewHolder = new DateViewHolder(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {

            case VIEW_TYPE_MESSAGE_SENT:
                Message message = (Message) getItem(position);
                UserTextViewHolder userTextViewHolder = (UserTextViewHolder) holder;
                userTextViewHolder.bind(message);
                break;

            case VIEW_TYPE_MESSAGE_RECEIVED:
                Message messageReceived = (Message) getItem(position);
                ReceivedTextViewHolder receivedTextViewHolder = (ReceivedTextViewHolder) holder;
                receivedTextViewHolder.bind(messageReceived);
                break;

            case ChatMessage.TYPE_DATE:
                ChatDate dateItem = (ChatDate) getItem(position);
                DateViewHolder dateViewHolder = (DateViewHolder) holder;
                dateViewHolder.bind(dateItem);
                break;
        }
    }

    public static class MessageDiff extends DiffUtil.ItemCallback<ChatMessage> {

        @Override
        public boolean areItemsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ChatMessage oldItem, @NonNull ChatMessage newItem) {
            return false;
        }
    }

    public static class UserTextViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView content, tvTime;

        public UserTextViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.chat_message_sent_content);
            tvTime = itemView.findViewById(R.id.chat_message_sent_time);
        }

        public void bind(Message message) {
            content.setText(message.getContent());
            tvTime.setText(timeFormatter.format(message.getTime()));
        }
    }

    public static class ReceivedTextViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView content, tvTime;

        public ReceivedTextViewHolder(View v) {
            super(v);
            content = itemView.findViewById(R.id.chat_message_rec_content);
            tvTime = itemView.findViewById(R.id.chat_message_rec_time);
        }

        public void bind(Message message) {
            content.setText(message.getContent());
            tvTime.setText(timeFormatter.format(message.getTime()));

        }
    }

    // ViewHolder for date row item
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView dateTextView;

        public DateViewHolder(View v) {
            super(v);
            dateTextView = v.findViewById(R.id.chat_date_label);
        }

        public void bind(ChatDate date) {
            dateTextView.setText(date.getDate());
        }
    }

}
