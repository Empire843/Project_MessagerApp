package com.example.asfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asfinal.R;
import com.example.asfinal.model.Message;
import com.example.asfinal.model.User;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView last_sender_name;
        TextView last_content;
        TextView sender_name_conversation;

        //        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            last_sender_name = itemView.findViewById(R.id.last_sender_name);
            last_content = itemView.findViewById(R.id.last_content);
            sender_name_conversation = itemView.findViewById(R.id.sender_name_conversation);
            itemView.setOnClickListener(this);
//            imageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View view) {
            if (conversationListener != null) {
                conversationListener.onClickConversation(view, getAdapterPosition());
            }
        }
    }

    public interface ConversationListener {
        void onClickConversation(View view, int position);
    }
}
