package com.example.asfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asfinal.R;
import com.example.asfinal.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENT_MESSAGE = 0;
    private static final int RECEIVED_MESSAGE = 1;
    private List<Message> messageList;
    private String uidCurrent;

    public MessageAdapter(List<Message> messageList, String uidCurrent) {
        this.messageList = messageList;
        this.uidCurrent = uidCurrent;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(uidCurrent)) {
            return SENT_MESSAGE;
        } else {
            return RECEIVED_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case SENT_MESSAGE:
                view = inflater.inflate(R.layout.message_item_send, parent, false);
                return new SentMessageViewHolder(view);
            case RECEIVED_MESSAGE:
                view = inflater.inflate(R.layout.message_item_receive, parent, false);
                return new ReceivedMessageViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        long time = message.getTimestamp();
        switch (holder.getItemViewType()) {
            case SENT_MESSAGE:
                SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
                sentHolder.messageText.setText(message.getContent());
//                sentHolder.messageTime.setText(time + "");
//                sentHolder.messageStatus.setImageResource(message.getStatusIcon());
                break;
            case RECEIVED_MESSAGE:
                ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
                receivedHolder.messageText.setText(message.getContent());
//                receivedHolder.messageTime.setText(message.getTimestamp());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;

        //        ImageView messageStatus;
        public SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_send);
//            messageTime = itemView.findViewById(R.id.message_time_send);
//            messageStatus = itemView.findViewById(R.id.message_status);
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_receive);
//            messageTime = itemView.findViewById(R.id.message_time_receive);
        }
    }
}
