package com.example.asfinal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asfinal.R;
import com.example.asfinal.model.User;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<User> resultList;
    private ConversationListener conversationListener;

    public ConversationAdapter() {
        this.resultList = new ArrayList<>();
    }

    public void setConversationListener(ConversationListener conversationListener) {
        this.conversationListener = conversationListener;
    }

    public void setList(List<User> resultList) {
        this.resultList = resultList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation_search, parent, false);
        return new ConversationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        User result = resultList.get(position);
        holder.last_sender_name.setText(result.getFull_name());
        holder.last_content.setText(result.getEmail());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView last_sender_name;
        TextView last_content;

        //        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            last_sender_name = itemView.findViewById(R.id.last_sender_name);
            last_content = itemView.findViewById(R.id.last_content);
//            imageView = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(this);
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
