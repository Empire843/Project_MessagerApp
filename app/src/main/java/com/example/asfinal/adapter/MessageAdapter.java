package com.example.asfinal.adapter;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asfinal.EditProfileActivity;
import com.example.asfinal.ProfileActivity;
import com.example.asfinal.R;
import com.example.asfinal.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENT_MESSAGE = 0;
    private static final int RECEIVED_MESSAGE = 1;
    private String imageUrl_sender;
    private String imageUrl_receiver;
    private List<Message> messageList;
    private String uidCurrent;

    private MessageListener messageListener;

    public void setMessageListener(MessageAdapter.MessageListener messageListener) {
        this.messageListener = messageListener;
        notifyDataSetChanged();
    }

    public MessageAdapter() {
        notifyDataSetChanged();
    }

    public MessageAdapter(List<Message> messageList, String uidCurrent) {
        this.messageList = messageList;
        this.uidCurrent = uidCurrent;
        notifyDataSetChanged();
    }

    public void setMessageList(List<Message> messageList, String uidCurrent, String imageUrl_sender, String imageUrl_receiver) {
        this.messageList = messageList;
        this.uidCurrent = uidCurrent;
        this.imageUrl_sender = imageUrl_sender;
        this.imageUrl_receiver = imageUrl_receiver;
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date(time);
        String formattedDate = sdf.format(date);
        switch (holder.getItemViewType()) {
            case SENT_MESSAGE:
                SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
                sentHolder.messageText.setText(message.getContent());
                sentHolder.messageTime.setText(formattedDate);
                String imageUrlSender = message.getImages();
                if (imageUrlSender != null) {
                    sentHolder.messageText.setVisibility(View.GONE);
                    sentHolder.messageImage.setVisibility(View.VISIBLE);
                    Glide.with(sentHolder.messageImage.getContext())
                            .load(imageUrlSender)
                            .placeholder(R.drawable.ic_user)
                            .into(sentHolder.messageImage);
                } else {
                    sentHolder.messageImage.setVisibility(View.GONE);
                }
                Glide.with(sentHolder.imageView_sender.getContext())
                        .load(imageUrl_sender)
                        .placeholder(R.drawable.ic_user)
//                                .error(R.drawable.error) // Ảnh hiển thị khi có lỗi xảy ra trong quá trình tải ảnh
                        .into(sentHolder.imageView_sender);
                break;
            case RECEIVED_MESSAGE:
                ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
                receivedHolder.messageText.setText(message.getContent());
                receivedHolder.messageTime.setText(formattedDate);
                String imageUrlReceiver = message.getImages();
                if (imageUrlReceiver != null) {
                    receivedHolder.messageText.setVisibility(View.GONE);
                    receivedHolder.messageImage.setVisibility(View.VISIBLE);
                    Glide.with(receivedHolder.messageImage.getContext())
                            .load(imageUrlReceiver)
                            .placeholder(R.drawable.ic_user)
                            .into(receivedHolder.messageImage);
                } else {
                    receivedHolder.messageImage.setVisibility(View.GONE);
                }

                Glide.with(receivedHolder.imageView_receiver.getContext())
                        .load(imageUrl_receiver)
                        .placeholder(R.drawable.ic_user)
//                                .error(R.drawable.error) // Ảnh hiển thị khi có lỗi xảy ra trong quá trình tải ảnh
                        .into(receivedHolder.imageView_receiver);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView messageText;
        TextView messageTime;
        ImageView messageImage;
        CircleImageView imageView_sender;

        public SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_send);
            imageView_sender = itemView.findViewById(R.id.profile_image_sender);
            messageTime = itemView.findViewById(R.id.message_time_send);
            messageImage = itemView.findViewById(R.id.message_image_send);
//            messageStatus = itemView.findViewById(R.id.message_status);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageListener != null) {
                messageListener.onClickMessage(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (messageListener != null) {
                messageListener.onLongClickMessage(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView messageText;
        TextView messageTime;
        ImageView messageImage;
        CircleImageView imageView_receiver;

        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text_receive);
            imageView_receiver = itemView.findViewById(R.id.profile_image_receiver);
            messageTime = itemView.findViewById(R.id.message_time_receive);
            messageImage = itemView.findViewById(R.id.message_image_receive);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (messageListener != null) {
                messageListener.onClickMessage(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (messageListener != null) {
                messageListener.onLongClickMessage(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public interface MessageListener {
        void onClickMessage(View view, int position);

        void onLongClickMessage(View view, int position);
    }
}
