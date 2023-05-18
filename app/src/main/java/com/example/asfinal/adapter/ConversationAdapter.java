package com.example.asfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asfinal.R;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<User> resultList;
    private List<Message> messageList;
    private Message message;
    private String full_name1;
    private ConversationListener conversationListener;

    public ConversationAdapter() {
        this.resultList = new ArrayList<>();
    }

    public void setConversationListener(ConversationListener conversationListener) {
        this.conversationListener = conversationListener;
        notifyDataSetChanged();
    }

    public void setList(List<User> resultList, Message message, String full_name1) {
        this.resultList = resultList;
        this.full_name1 = full_name1;
        this.message = message;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        User result = resultList.get(position);
//
        holder.last_sender_name.setText(result.getFull_name() + ":");
        holder.last_content.setText("the last message");
        holder.sender_name_conversation.setText(result.getFull_name());
        if (result.getAvatar() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(result.getAvatar())
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

//    public void deleteItem(int position) {
//        messageList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, getItemCount());
//    }

    public void updateData(List<Message> newData) {
        messageList.clear();
        messageList.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView last_sender_name;
        TextView last_content;
        TextView sender_name_conversation;
        CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            last_sender_name = itemView.findViewById(R.id.last_sender_name);
            last_content = itemView.findViewById(R.id.last_content);
            sender_name_conversation = itemView.findViewById(R.id.sender_name_conversation);
            imageView = itemView.findViewById(R.id.profile_image_conversation);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (conversationListener != null) {
                conversationListener.onClickConversation(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (conversationListener != null) {
                conversationListener.onLongClickConversation(view, getAdapterPosition());
                return true;
            }
            return false;
        }

    }

    public interface ConversationListener {
        void onClickConversation(View view, int position);

        void onLongClickConversation(View view, int position);
    }

}
